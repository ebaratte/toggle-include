package com.etienne.toggleinclude;

import com.intellij.codeInspection.InspectionToolProvider;

public class ConvertIncludeInspectionProvider implements InspectionToolProvider {
    @Override
    public Class[] getInspectionClasses() {
        return new Class[]{ConvertIncludeInspection.class};
    }
}
