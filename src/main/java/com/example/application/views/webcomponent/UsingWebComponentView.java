package com.example.application.views.webcomponent;

import com.example.application.backend.domain.Country;
import com.example.application.backend.service.CoronaService;
import com.example.application.backend.service.GeoIpService;
import com.example.application.component.CoronaChart;
import com.example.application.component.MapChart;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
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
@CssImport("./styles/views/webcomponent/using-web-component-view.css")
public class UsingWebComponentView extends HorizontalLayout {

    @Autowired
    private CoronaService coronaService;

    @Autowired
    private CoronaChart coronaChart;

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

        coronaChart.addCountrySelectorListener(createCountrySelectorListener(mapChart));
        coronaChart.setCountry(coronaService.getById(GeoIpService.WORLD_ISO_CODE));

        VerticalLayout mapChartWrapper = createWrapperWithCaption("React component", mapChart);
        mapChartWrapper.addClassName("react-component-layout");
        VerticalLayout coronaChartWrapper = createWrapperWithCaption("Vaadin component", coronaChart);
        coronaChartWrapper.addClassName("vaadin-component-layout");

        Board board = new Board();
        board.setMaxWidth("none");
        board.addRow(mapChartWrapper, coronaChartWrapper);
        add(board);
    }

    private HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<Country>, Country>> createCountrySelectorListener(MapChart mapChart) {
        return event -> {
            if (event.isFromClient()) {
                mapChart.getElement().setProperty("selectedCountryIsoCode", event.getValue().getIsoCode());
            }
        };
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
