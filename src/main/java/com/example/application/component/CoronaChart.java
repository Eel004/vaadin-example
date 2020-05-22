package com.example.application.component;

import com.example.application.backend.domain.Country;
import com.example.application.backend.domain.Day;
import com.example.application.backend.service.CoronaService;
import com.example.application.backend.service.GeoIpService;
import com.example.application.backend.ui.DashboardChart;
import com.example.application.backend.ui.DashboardStats;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Scope("prototype")
public class CoronaChart extends VerticalLayout {

    @Autowired
    private CoronaService coronaService;
    @Autowired
    private GeoIpService geoIpService;

    private Row overviewRow = new Row();
    private Row chartRow = new Row();
    private ComboBox<Country> countrySelector;
    private HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<Country>, Country>> countrySelectorListener;

    public CoronaChart() {
        this(null);
    }

    public CoronaChart(HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<Country>, Country>> countrySelectorListener) {
        this.countrySelectorListener = countrySelectorListener;
    }

    @PostConstruct
    public void init() {
        countrySelector = new ComboBox<>();
        countrySelector.setItems(coronaService.findAll());
        countrySelector.setItemLabelGenerator(Country::getName);
        countrySelector.setPlaceholder("Country");

        Board board = new Board();
        board.addRow(countrySelector);
        board.addRow(overviewRow);
        board.addRow(chartRow);
        add(board);

        if (countrySelectorListener != null) {
            countrySelector.addValueChangeListener(countrySelectorListener);
        } else {
            countrySelector.addValueChangeListener(event -> {
                if (event.isFromClient()) {
                    setCountry(coronaService.getById(event.getValue().getIsoCode()));
                }
            });
        }
    }

    public void addCountrySelectorListener(HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<Country>, Country>> listener) {
        countrySelector.addValueChangeListener(listener);
    }

    public Country getCurrentCountry() {
        return countrySelector.getValue();
    }

    public void setCountry(Country country) {
        if (country != null) {
            countrySelector.setValue(country);
            overviewRow.removeAll();

            if (country.getPopulation() != null && country.getPopulation() != 0) {
                overviewRow.add(
                        new DashboardStats("Population", country.getPopulation(), null, "number-population")
                );
            }
            overviewRow.add(
                    new DashboardStats("Cases", country.getTotalCases(), country.getPopulation(), "number-cases"),
                    new DashboardStats("Deaths", country.getTotalDeaths(), country.getTotalCases(), "number-deaths"),
                    new DashboardStats("Recovered", country.getTotalRecovered(), country.getTotalCases(), "number-recovered")
            );
            chartRow.removeAll();
            chartRow.add(new DashboardChart(
                    "Cumulative",
                    ChartType.SPLINE,
                    country.getDays(),
                    Day::getCases,
                    Day::getDeaths,
                    Day::getRecovered
            ));

            if (country.getDays().size() >= 2) {
                int days;
                if (country.getDays().size() <= 7) {
                    days = country.getDays().size();
                } else {
                    days = 8;
                }
                List<Day> timeline = country.getDays().subList(0, days);
                chartRow.add(new DashboardChart(
                        "Daily",
                        ChartType.COLUMN,
                        timeline,
                        Day::getNewCases,
                        Day::getNewDeaths,
                        Day::getNewRecovered
                ));
            }

        }
    }

}
