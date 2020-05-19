package com.example.application.backend.ui;

import com.example.application.backend.domain.Country;
import com.example.application.backend.domain.Day;
import com.example.application.backend.service.CoronaService;
import com.example.application.backend.service.GeoIpService;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinRequest;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
//@CssImport(value = "./styles/views/corona/styles.css", include = "vaadin-chart-default-theme")
@CssImport(value = "./styles/views/corona/charts.css", themeFor = "vaadin-chart", include = "vaadin-chart-default-theme")
@Route(value = "coronadashboard", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
public class CoronaDashboard extends Div implements HasUrlParameter<String>, AfterNavigationObserver, HasDynamicTitle {

    @Autowired
    private CoronaService coronaService;

    @Autowired
    private GeoIpService geoIpService;

    private Row overviewRow = new Row();
    private Row chartRow = new Row();
    private ComboBox<Country> countrySelector;

    private String selectedCountryCode;


    public CoronaDashboard(GeoIpService geoIpService, CoronaService covidService) {
        this.geoIpService = geoIpService;
        this.coronaService = covidService;
        HorizontalLayout title = new HorizontalLayout(new H1("Covid-19 Dashboard"));
        title.addClassName("title");

        countrySelector = new ComboBox<>();
        countrySelector.setItems(covidService.findAll());
        countrySelector.setItemLabelGenerator(Country::getName);
        countrySelector.setPlaceholder("Country");
        countrySelector.setWidth("400px");

        Board board = new Board();
        board.addRow(countrySelector);
        board.addRow(overviewRow);
        board.addRow(chartRow);
        add(board);

        countrySelector.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                UI.getCurrent().navigate(CoronaDashboard.class, countrySelector.getValue().getIsoCode());
            }
        });
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String isoCode) {
        String ip = getIP();
        selectedCountryCode = isoCode;
        if (selectedCountryCode == null || selectedCountryCode.isEmpty()) {
            selectedCountryCode = geoIpService.getIsoCode(ip);
        }

        try {
            log.info(String.format("IP - ISO code: %s - %s", ip, isoCode));
            setCountry(coronaService.getById(selectedCountryCode));
        } catch (FeignException e) {
            log.info("Cannot find ISO code: " + isoCode);
            setCountry(coronaService.getById(GeoIpService.WORLD_ISO_CODE));
            Notification.show("Country not found. Showing global data.", 5000, Notification.Position.MIDDLE);

        } catch (Exception e) {
            log.error("Error fetching data", e);
            Notification.show("Error fetching data.", 5000, Notification.Position.MIDDLE);
        }
    }

    private String getIP() {
        String ip;

        if ((ip = VaadinRequest.getCurrent().getHeader("X-Forwarded-For")) == null) {
            if ((ip = VaadinRequest.getCurrent().getHeader("Via")) == null) {
                ip = VaadinRequest.getCurrent().getRemoteHost();
            }
        }

        return ip;
    }

    @Override
    public String getPageTitle() {
        return "Covid Dashboard - " + countrySelector.getValue().getName();
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

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        setCountry(coronaService.getById(selectedCountryCode));
    }
}
