/*
 * ATLauncher - https://github.com/ATLauncher/ATLauncher
 * Copyright (C) 2013-2022 ATLauncher
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.atlauncher.gui.card;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.mini2Dx.gettext.GetText;

import com.atlauncher.App;
import com.atlauncher.data.Instance;
import com.atlauncher.data.modrinth.ModrinthDependency;
import com.atlauncher.data.modrinth.ModrinthProject;
import com.atlauncher.gui.dialogs.ModrinthVersionSelectorDialog;
import com.atlauncher.network.Analytics;
import com.atlauncher.utils.ModrinthApi;
import com.atlauncher.utils.Utils;
import com.atlauncher.workers.BackgroundImageWorker;

@SuppressWarnings("serial")
public final class ModrinthProjectDependencyCard extends JPanel {
    private final ModrinthVersionSelectorDialog parent;
    private final ModrinthDependency dependency;
    private final Instance instance;

    public ModrinthProjectDependencyCard(ModrinthVersionSelectorDialog parent, ModrinthDependency dependency,
            Instance instance) {
        this.parent = parent;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 180));

        this.dependency = dependency;
        this.instance = instance;

        setupComponents();
    }

    private void setupComponents() {
        ModrinthProject mod = ModrinthApi.getProject(dependency.projectId);

        JPanel summaryPanel = new JPanel(new BorderLayout());
        JTextArea summary = new JTextArea();
        summary.setText(mod.description);
        summary.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        summary.setEditable(false);
        summary.setHighlighter(null);
        summary.setLineWrap(true);
        summary.setWrapStyleWord(true);
        summary.setEditable(false);

        JLabel icon = new JLabel(Utils.getIconImage("/assets/image/no-icon.png"));
        icon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        icon.setVisible(false);

        summaryPanel.add(icon, BorderLayout.WEST);
        summaryPanel.add(summary, BorderLayout.CENTER);
        summaryPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton(GetText.tr("Add"));
        JButton viewButton = new JButton(GetText.tr("View"));
        buttonsPanel.add(addButton);
        buttonsPanel.add(viewButton);

        addButton.addActionListener(e -> {
            Analytics.sendEvent(mod.title, "AddDependency", "ModrinthMod");
            new ModrinthVersionSelectorDialog(parent, mod, instance);
            parent.reloadDependenciesPanel();
        });

        viewButton.addActionListener(e -> String.format("https://modrinth.com/mod/%s", mod.slug));

        add(summaryPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        TitledBorder border = new TitledBorder(null, mod.title, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, App.THEME.getBoldFont().deriveFont(12f));
        setBorder(border);

        if (mod.iconUrl != null && !mod.iconUrl.isEmpty()) {
            new BackgroundImageWorker(icon, mod.iconUrl, 60, 60).execute();
        }
    }
}
