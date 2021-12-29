/*
 * Copyright (c) 2009-2020 Weasis Team and other contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.sr;

import java.util.Map;
import javax.swing.Icon;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Deactivate;
import org.weasis.core.api.explorer.DataExplorerView;
import org.weasis.core.api.explorer.model.DataExplorerModel;
import org.weasis.core.api.image.GridBagLayoutModel;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.util.ResourceUtil;
import org.weasis.core.api.util.ResourceUtil.FileIcon;
import org.weasis.core.ui.docking.UIManager;
import org.weasis.core.ui.editor.SeriesViewer;
import org.weasis.core.ui.editor.SeriesViewerFactory;
import org.weasis.core.ui.editor.ViewerPluginBuilder;
import org.weasis.dicom.explorer.DicomExplorer;
import org.weasis.dicom.explorer.DicomModel;

@org.osgi.service.component.annotations.Component(service = SeriesViewerFactory.class)
public class SRFactory implements SeriesViewerFactory {

  public static final String NAME = Messages.getString("SRFactory.viewer");

  public SRFactory() {
    super();
  }

  @Override
  public Icon getIcon() {
    return ResourceUtil.getIcon(FileIcon.TEXT);
  }

  @Override
  public String getUIName() {
    return NAME;
  }

  @Override
  public String getDescription() {
    return Messages.getString("SRFactory.sr");
  }

  @Override
  public SeriesViewer<?> createSeriesViewer(Map<String, Object> properties) {
    GridBagLayoutModel model = SRContainer.VIEWS_1x1;
    String uid = null;
    if (properties != null) {
      Object obj = properties.get(org.weasis.core.api.image.GridBagLayoutModel.class.getName());
      if (obj instanceof GridBagLayoutModel) {
        model = (GridBagLayoutModel) obj;
      }
      // Set UID
      Object val = properties.get(ViewerPluginBuilder.UID);
      if (val instanceof String) {
        uid = (String) val;
      }
    }

    SRContainer instance = new SRContainer(model, uid);
    if (properties != null) {
      Object obj = properties.get(DataExplorerModel.class.getName());
      if (obj instanceof DicomModel) {
        // Register the PropertyChangeListener
        DicomModel m = (DicomModel) obj;
        m.addPropertyChangeListener(instance);
      }
    }
    return instance;
  }

  public static void closeSeriesViewer(SRContainer mprContainer) {
    // Unregister the PropertyChangeListener
    DataExplorerView dicomView = UIManager.getExplorerplugin(DicomExplorer.NAME);
    if (dicomView != null) {
      dicomView.getDataExplorerModel().removePropertyChangeListener(mprContainer);
    }
  }

  @Override
  public boolean canReadMimeType(String mimeType) {
    return SRElementFactory.SERIES_SR_MIMETYPE.equals(mimeType);
  }

  @Override
  public boolean isViewerCreatedByThisFactory(SeriesViewer<? extends MediaElement> viewer) {
    return viewer instanceof SRContainer;
  }

  @Override
  public int getLevel() {
    return 25;
  }

  @Override
  public boolean canAddSeries() {
    return false;
  }

  @Override
  public boolean canExternalizeSeries() {
    return true;
  }

  // ================================================================================
  // OSGI service implementation
  // ================================================================================

  @Deactivate
  protected void deactivate(ComponentContext context) {
    UIManager.closeSeriesViewerType(SRContainer.class);
  }
}
