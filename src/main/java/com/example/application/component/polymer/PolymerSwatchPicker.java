package com.example.application.component.polymer;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;

@Tag("paper-swatch-picker")
@NpmPackage(value = "@polymer/paper-swatch-picker",
        version = "3.0.1")
@JsModule("@polymer/paper-swatch-picker/paper-swatch-picker.js")
public class PolymerSwatchPicker extends Component {
}
