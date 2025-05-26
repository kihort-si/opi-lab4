package org.viacheslav;

import jakarta.faces.context.FacesContext;

public abstract class FacesContextSetter extends FacesContext {
    public static void setCurrentInstance(FacesContext context) {
        FacesContext.setCurrentInstance(context);
    }
}
