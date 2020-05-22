package com.example.application.views.webcomponent;

import com.example.application.backend.service.CoronaService;
import com.example.application.component.CoronaChart;
import com.example.application.component.MapChart;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Route(value = "using-web-component", layout = MainView.class)
@PageTitle("Using Web Component")
public class UsingWebComponentView extends HorizontalLayout {

    @Autowired
    private CoronaChart coronaChart;

    @Autowired
    private CoronaService coronaService;

    @PostConstruct
    public void init() {
        setSpacing(true);

        MapChart mapChart = new MapChart();
        mapChart.setWidthFull();
        mapChart.getElement().addPropertyChangeListener("selectedCountryIsoCode", "click", e -> {
            Optional.ofNullable(e.getValue())
                    .map(Object::toString)
                    .ifPresent(parsedValue -> coronaChart.setCountry(coronaService.getById(parsedValue)));
        });

        VerticalLayout mapChartWrapper = createWrapperWithCaption("React component", mapChart);
        VerticalLayout coronaChartWrapper = createWrapperWithCaption("Vaadin component", coronaChart);

        Board board = new Board();
        board.setMaxWidth("none");
        board.addRow(mapChartWrapper, coronaChartWrapper);
        add(board);
    }

    private VerticalLayout createWrapperWithCaption(String caption, Component wrapped) {
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setSpacing(true);
        Label captionLabel = new Label(caption);
        Style captionLabelStyle = captionLabel.getElement().getStyle();
        captionLabelStyle.set("color", "var(--lumo-primary-text-color)");
        captionLabelStyle.set("font-size", "20px");
        captionLabelStyle.set("font-weight", "bold");
        wrapper.add(captionLabel, wrapped);
        return wrapper;
    }

}
