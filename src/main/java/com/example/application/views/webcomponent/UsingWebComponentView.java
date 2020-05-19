package com.example.application.views.webcomponent;

import com.example.application.component.MapChart;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "using-web-component", layout = MainView.class)
@PageTitle("Using Web Component")
public class UsingWebComponentView extends VerticalLayout {

    private static String[] helloButtonColors = {"red", "green", "blue", "yellow", "orange", "purple", "pink", "black"};

    public UsingWebComponentView() {
        getStyle().set("flex-direction", "column");

        Button changeBackgroundButton = new Button("Change background");
        Button getInputValueButton = new Button("Get input value");
        MapChart mapChart = new MapChart();

        add(changeBackgroundButton, getInputValueButton, mapChart);
    }

}
