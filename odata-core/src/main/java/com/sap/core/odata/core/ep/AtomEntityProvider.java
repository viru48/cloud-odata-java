package com.sap.core.odata.core.ep;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.core.odata.api.edm.Edm;
import com.sap.core.odata.api.edm.EdmEntitySet;
import com.sap.core.odata.api.edm.EdmLiteralKind;
import com.sap.core.odata.api.edm.EdmProperty;
import com.sap.core.odata.api.edm.EdmSimpleType;
import com.sap.core.odata.api.edm.EdmSimpleTypeKind;
import com.sap.core.odata.api.enums.MediaType;
import com.sap.core.odata.api.ep.ODataEntityContent;
import com.sap.core.odata.api.ep.ODataEntityProvider;
import com.sap.core.odata.api.ep.ODataEntityProviderException;
import com.sap.core.odata.api.processor.ODataContext;
import com.sap.core.odata.core.ep.aggregator.EntityInfoAggregator;
import com.sap.core.odata.core.ep.aggregator.EntityPropertyInfo;

// TODO usage of "ByteArrayInputStream(out.toByteArray())":  check synchronized call / copy of data
public class AtomEntityProvider extends ODataEntityProvider {

  private static final Logger LOG = LoggerFactory.getLogger(AtomEntityProvider.class);

  AtomEntityProvider(ODataContext ctx) throws ODataEntityProviderException {
    super(ctx);
  }

  @Override
  public ODataEntityContent writeServiceDocument(Edm edm, String serviceRoot) throws ODataEntityProviderException {
    OutputStreamWriter writer = null;
    ODataEntityContentImpl content = new ODataEntityContentImpl();

    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      writer = new OutputStreamWriter(outputStream, "utf-8");
      AtomServiceDocumentProvider.writeServiceDocument(edm, serviceRoot, writer);

      content.setContentStream(new ByteArrayInputStream(outputStream.toByteArray()));
      content.setContentHeader(MediaType.APPLICATION_ATOM_SVC.toString());

      return content;
    } catch (UnsupportedEncodingException e) {
      throw new ODataEntityProviderException(ODataEntityProviderException.COMMON, e);
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException e) {
          // don't throw in finally!  
          LOG.error(e.getLocalizedMessage(), e);
        }
      }
    }
  }

  @Override
  public ODataEntityContent writeEntry(EdmEntitySet entitySet, Map<String, Object> data, String mediaResourceMimeType) throws ODataEntityProviderException {
    ByteArrayOutputStream outStream = null;
    ODataEntityContentImpl content = new ODataEntityContentImpl();

    try {
      AtomEntryEntityProvider as = new AtomEntryEntityProvider(this.getContext());
      EntityInfoAggregator eia = EntityInfoAggregator.create(entitySet);

      outStream = new ByteArrayOutputStream();
      XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outStream, "utf-8");
      as.append(writer, eia, data, true, mediaResourceMimeType);

      writer.flush();
      outStream.flush();
      outStream.close();

      content.setContentStream(new ByteArrayInputStream(outStream.toByteArray()));
      content.setContentHeader(MediaType.APPLICATION_ATOM_XML_ENTRY.toString());
      content.setETag(as.getETag());

      return content;
    } catch (Exception e) {
      throw new ODataEntityProviderException(ODataEntityProviderException.COMMON, e);
    } finally {
      if (outStream != null) {
        try {
          outStream.close();
        } catch (IOException e) {
          // don't throw in finally!  
          LOG.error(e.getLocalizedMessage(), e);
        }
      }
    }
  }

  @Override
  public ODataEntityContent writeProperty(EdmProperty edmProperty, Object value) throws ODataEntityProviderException {
    ByteArrayOutputStream outStream = null;
    ODataEntityContentImpl content = new ODataEntityContentImpl();

    try {
      XmlPropertyEntityProvider ps = new XmlPropertyEntityProvider();
      EntityPropertyInfo propertyInfo = EntityInfoAggregator.create(edmProperty);

      outStream = new ByteArrayOutputStream();
      XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outStream, "utf-8");
      ps.append(writer, propertyInfo, value, true);

      writer.flush();
      outStream.flush();
      outStream.close();

      content.setContentStream(new ByteArrayInputStream(outStream.toByteArray()));
      content.setContentHeader(MediaType.APPLICATION_XML.toString());

      return content;
    } catch (Exception e) {
      throw new ODataEntityProviderException(ODataEntityProviderException.COMMON, e);
    } finally {
      if (outStream != null) {
        try {
          outStream.close();
        } catch (IOException e) {
          // don't throw in finally!  
          LOG.error(e.getLocalizedMessage(), e);
        }
      }
    }
  }

  @Override
  public ODataEntityContent writeFeed(EdmEntitySet entitySet, List<Map<String, Object>> data, String mediaResourceMimeType) throws ODataEntityProviderException {
    return null;
  }

  @Override
  public ODataEntityContent writeText(EdmProperty edmProperty, Object value) throws ODataEntityProviderException {
    ODataEntityContentImpl content = new ODataEntityContentImpl();

    try {
      Map<?, ?> mappedData = Collections.emptyMap();

      if (value instanceof Map) {
        mappedData = (Map<?, ?>) value;
        value = mappedData.get(edmProperty.getName());
      } 

      EdmSimpleType type = (EdmSimpleType) edmProperty.getType();
      String stringValue = type.valueToString(value, EdmLiteralKind.DEFAULT, edmProperty.getFacets());
      if (stringValue == null) {
        stringValue = "";
      }

      ByteArrayInputStream bais = new ByteArrayInputStream(stringValue.getBytes("UTF-8"));
      content.setContentStream(bais);

      String contentHeader = MediaType.TEXT_PLAIN.toString();
      if (type == EdmSimpleTypeKind.Binary.getEdmSimpleTypeInstance()) {
        if (edmProperty.getMimeType() != null) {
          contentHeader = edmProperty.getMimeType();
        } else {
          if (edmProperty.getMapping() != null && edmProperty.getMapping().getMimeType() != null) {
            String mimeTypeMapping = edmProperty.getMapping().getMimeType();
            contentHeader = (String) mappedData.get(mimeTypeMapping);
          }
        }
      }

      content.setContentHeader(contentHeader);

      return content;
    } catch (Exception e) {
      throw new ODataEntityProviderException(ODataEntityProviderException.COMMON, e);
    }
  }

  @Override
  public ODataEntityContent writeMediaResource(EdmProperty edmProperty, Object value) throws ODataEntityProviderException {
    return null;
  }

}
