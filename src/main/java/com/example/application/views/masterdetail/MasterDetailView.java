package com.example.application.views.masterdetail;

import com.example.application.backend.domain.Country;
import com.example.application.backend.service.CoronaService;
import com.example.application.views.dashboard.WrapperCard;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Route(value = "overview", layout = MainView.class)
@PageTitle("overview")
@CssImport("./styles/views/masterdetail/master-detail-view.css")
@HtmlImport(".styles/views/masterdetail/grid-styles.html")
@RouteAlias(value = "", layout = MainView.class)
@Slf4j
public class MasterDetailView extends Div implements AfterNavigationObserver {

    public static final String NUMBER_FORMAT = "%,d";
    private Grid<Country> grid;

    private ListDataProvider<Country> dataProvider;

    @Autowired
    private CoronaService coronaService;

    private H2 totalCasesH2 = new H2();
    private H2 totalRecoveredH2 = new H2();
    private H2 totalDeathH2 = new H2();

    @PostConstruct
    public void init() {
        setId("master-detail-view");
        grid = new Grid<>();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setColumnReorderingAllowed(true);
        grid.setVerticalScrollingEnabled(true);
        grid.setHeightFull();
        Grid.Column<Country> countryCol = grid.addColumn(Country::getName).setHeader("Country");
        Grid.Column<Country> populationCol = grid.addColumn(new NumberRenderer<>(Country::getPopulation, NUMBER_FORMAT))
                .setHeader("Population");
        Grid.Column<Country> totalCasesCol = grid.addColumn(new NumberRenderer<>(Country::getTotalCases, NUMBER_FORMAT)).setHeader("Total cases")
                .setClassNameGenerator(item -> {
                   if (item.getTotalCases() >= 10000) {
                        return "red-text";
                   }
                   return null;
                });
        Grid.Column<Country> recoveredCol = grid.addColumn(new NumberRenderer<>(Country::getTotalRecovered, NUMBER_FORMAT)).setHeader("Total recovered");
        Grid.Column<Country> deathCol = grid.addColumn(new NumberRenderer<>(Country::getTotalDeaths, NUMBER_FORMAT)).setHeader("Total deaths");

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(countryCol).setComponent(createCountryFilter());
        headerRow.getCell(populationCol).setComponent(createPopulationFilter());
        headerRow.getCell(totalCasesCol).setComponent(createTotalCaseFilter());
        headerRow.getCell(recoveredCol).setComponent(createRecoveredCaseFilter());
        headerRow.getCell(deathCol).setComponent(createDeathFilter());
        grid.getColumns().forEach(col -> {
            col.setSortable(true);
            col.setResizable(true);
        });
        add(createSummaryBoard(), grid);
    }

    private ComboBox<Country> createCountryFilter() {
        ComboBox<Country> searchSelector = new ComboBox<>();
        searchSelector.setItems(coronaService.findAll());
        searchSelector.setItemLabelGenerator(Country::getName);
        searchSelector.setWidth("200px");
        searchSelector.addValueChangeListener(event -> {
           if (event.isFromClient()) {
               dataProvider.clearFilters();
               final Country country = event.getValue();
               if (country != null) {
                   dataProvider.addFilter(item -> item.getName().equals(country.getName()));
               }
           }
        });
        return searchSelector;
    }

    private TextField createPopulationFilter() {
        TextField toReturn = new TextField();
        toReturn.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                final String value = event.getValue();
                if (StringUtils.isNotBlank(value)) {
                    dataProvider.addFilter(item -> {
                        Long population = item.getPopulation();
                        if (population != null) {
                            return population >= Long.parseLong(value);
                        }
                        return false;
                    });
                }
            }
        });
        return toReturn;
    }

    private TextField createTotalCaseFilter() {
        TextField totalCaseField = new TextField();
        totalCaseField.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                final String value = event.getValue();
                if (StringUtils.isNotBlank(value)) {
                    dataProvider.addFilter(item -> item.getTotalCases() >= Long.parseLong(value));
                }
            }
        });
        return totalCaseField;
    }

    private TextField createRecoveredCaseFilter() {
        TextField recoveredField = new TextField();
        recoveredField.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                final String value = event.getValue();
                if (StringUtils.isNotBlank(value)) {
                    dataProvider.addFilter(item -> item.getTotalRecovered() >= Long.parseLong(value));
                }
            }
        });
        return recoveredField;
    }

    private TextField createDeathFilter() {
        TextField deathField = new TextField();
        deathField.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                final String value = event.getValue();
                if (StringUtils.isNotBlank(value)) {
                    dataProvider.addFilter(item -> item.getTotalDeaths() >= Long.parseLong(value));
                }
            }
        });
        return deathField;
    }

    private Board createSummaryBoard() {
        Board board = new Board();
        board.addRow(
                createBadge("Total cases", totalCasesH2, "primary-text", "", "badge"),
                createBadge("Recovered cases", totalRecoveredH2, "success-text", "", "badge success"),
                createBadge("Death cases", totalDeathH2, "error-text","", "badge error")
        );
        return board;
    }

    private WrapperCard createBadge(String title, H2 h2, String h2ClassName,
                                    String description, String badgeTheme) {
        Span titleSpan = new Span(title);
        titleSpan.getElement().setAttribute("theme", badgeTheme);

        h2.addClassName(h2ClassName);

        Span descriptionSpan = new Span(description);
        descriptionSpan.addClassName("secondary-text");

        return new WrapperCard("wrapper",
                new Component[] { titleSpan, h2, descriptionSpan }, "card",
                "space-m");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        Country global = coronaService.getById("global");
        totalCasesH2.setText(String.format(NUMBER_FORMAT, global.getTotalCases()));
        totalRecoveredH2.setText(String.format(NUMBER_FORMAT, global.getTotalRecovered()));
        totalDeathH2.setText(String.format(NUMBER_FORMAT, global.getTotalDeaths()));
        dataProvider = new ListDataProvider<>(coronaService.findAll());
        grid.setDataProvider(dataProvider);
    }

}
