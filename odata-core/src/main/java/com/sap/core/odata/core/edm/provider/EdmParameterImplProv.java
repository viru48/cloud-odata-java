package com.sap.core.odata.core.edm.provider;

import com.sap.core.odata.api.edm.EdmAnnotatable;
import com.sap.core.odata.api.edm.EdmAnnotations;
import com.sap.core.odata.api.edm.EdmException;
import com.sap.core.odata.api.edm.EdmParameter;
import com.sap.core.odata.api.edm.EdmType;
import com.sap.core.odata.api.edm.provider.FunctionImportParameter;
import com.sap.core.odata.core.edm.EdmSimpleTypeFacadeImpl;

/**
 * @author SAP AG
 */
public class EdmParameterImplProv extends EdmElementImplProv implements EdmParameter, EdmAnnotatable {

  FunctionImportParameter parameter;

  public EdmParameterImplProv(EdmImplProv edm, FunctionImportParameter parameter) throws EdmException {
    super(edm, parameter.getName(), parameter.getType().getFullQualifiedName(), parameter.getFacets(), parameter.getMapping());
    this.parameter = parameter;
  }

  @Override
  public EdmType getType() throws EdmException {

    return EdmSimpleTypeFacadeImpl.getEdmSimpleType(parameter.getType());

  }

  @Override
  public EdmAnnotations getAnnotations() throws EdmException {
    return new EdmAnnotationsImplProv(parameter.getAnnotationAttributes(), parameter.getAnnotationElements());
  }
}