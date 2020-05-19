package com.example.application.views.webcomponent;

import com.example.application.component.CoronaChart;
import com.example.application.component.MapChart;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Route(value = "using-web-component", layout = MainView.class)
@PageTitle("Using Web Component")
public class UsingWebComponentView extends HorizontalLayout {

    @Autowired
    private CoronaChart coronaChart;

    @PostConstruct
    public void init() {
        setSpacing(true);

        MapChart mapChart = new MapChart();
        mapChart.setWidthFull();

        add(mapChart, coronaChart);
    }

}
