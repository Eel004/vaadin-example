package com.example.application.views.dashboard;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.Crosshair;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.example.application.views.main.MainView;

@Route(value = "dashboard1", layout = MainView.class)
@RouteAlias(value = "dashboard-old", layout = MainView.class)
@PageTitle("Dashboard")
@CssImport(value = "./styles/views/dashboard/dashboard-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class DashboardView extends Div implements AfterNavigationObserver {

    private Grid<HealthGridItem> grid = new Grid<>();

    private Chart monthlyVisitors = new Chart();
    private Chart responseTimes = new Chart();
    private final H2 usersH2 = new H2();
    private final H2 eventsH2 = new H2();
    private final H2 conversionH2 = new H2();

    public DashboardView() {
        setId("dashboard-view");
        Board board = new Board();
        board.addRow(
                createBadge("Employees", usersH2, "primary-text", "Current users in the app", "badge"),
                createBadge("Events", eventsH2, "success-text", "Events from the views", "badge success"),
                createBadge("Conversion", conversionH2, "error-text","User conversion rate", "badge error")
        );

        monthlyVisitors.getConfiguration()
                .setTitle("Languages of each department");
        monthlyVisitors.getConfiguration().getChart().setType(ChartType.COLUMN);
        WrapperCard monthlyVisitorsWrapper = new WrapperCard("wrapper",
                new Component[] { monthlyVisitors }, "card");
        board.add(monthlyVisitorsWrapper);

        grid.addColumn(HealthGridItem::getCity).setHeader("City");
        grid.addColumn(new ComponentRenderer<>(item -> {
            Span span = new Span(item.getStatus());
            span.getElement().getThemeList().add(item.getTheme());
            return span;
        })).setFlexGrow(0).setWidth("100px").setHeader("Status");
        grid.addColumn(HealthGridItem::getItemDate).setHeader("Date")
                .setWidth("140px");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        WrapperCard gridWrapper = new WrapperCard("wrapper",
                new Component[] { new H3("Service health"), grid }, "card");
        responseTimes.getConfiguration().setTitle("Response times");
        WrapperCard responseTimesWrapper = new WrapperCard("wrapper",
                new Component[] { responseTimes }, "card");
        board.addRow(gridWrapper, responseTimesWrapper);

        add(board);
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

        // Set some data when this view is displayed.

        // Top row widgets
        usersH2.setText("745");
        eventsH2.setText("54.6k");
        conversionH2.setText("18%");

        // First chart
        Configuration configuration = monthlyVisitors.getConfiguration();
        configuration.addSeries(new ListSeries("A", 3, 4, 10, 6, 2, 5, 1));
        configuration.addSeries(new ListSeries("B", 1, 4, 2, 6, 0, 3, 1));
        configuration.addSeries(new ListSeries("C", 3, 4, 1, 6, 0, 0, 1));
        configuration.addSeries(new ListSeries("D", 10, 1, 5, 6, 0, 0, 1));
        configuration.addSeries(new ListSeries("E", 0, 4, 5, 90, 0, 0, 1));

        XAxis x = new XAxis();
        x.setCrosshair(new Crosshair());
        x.setCategories("Java", "Python", "Go", ".Net", "PHP", "Angular", "React");
        configuration.addxAxis(x);

        YAxis y = new YAxis();
        y.setMin(0);
        configuration.addyAxis(y);

        // Grid
        List<HealthGridItem> gridItems = new ArrayList<>();
        gridItems.add(new HealthGridItem(LocalDate.of(2019, 1, 14), "M\u00FCnster", "Germany", "Good", "badge"));
        gridItems.add(new HealthGridItem(LocalDate.of(2019, 1, 14), "Cluj-Napoca", "Romania", "Good", "badge"));
        gridItems.add(new HealthGridItem(LocalDate.of(2019, 1, 14), "Ciudad Victoria", "Mexico", "Good", "badge"));
        gridItems.add(new HealthGridItem(LocalDate.of(2019, 1, 14), "Ebetsu", "Japan", "Excellent", "badge success"));
        gridItems
                .add(new HealthGridItem(LocalDate.of(2019, 1, 14), "S\u00E3o Bernardo do Campo", "Brazil", "Good", "badge"));
        gridItems.add(new HealthGridItem(LocalDate.of(2019, 1, 14), "Maputo", "Mozambique", "Good", "badge"));
        gridItems.add(new HealthGridItem(LocalDate.of(2019, 1, 14), "Warsaw", "Poland", "Good", "badge"));
        gridItems.add(new HealthGridItem(LocalDate.of(2019, 1, 14), "Kasugai", "Japan", "Failing", "badge error"));
        gridItems.add(new HealthGridItem(LocalDate.of(2019, 1, 14), "Lancaster", "United States", "Excellent",
                "badge success"));

        grid.setItems(gridItems);

        // Second chart
        configuration = responseTimes.getConfiguration();
        configuration
                .addSeries(new ListSeries("Tokyo", 7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6));
        configuration
                .addSeries(new ListSeries("London", 3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8));

        x = new XAxis();
        x.setCrosshair(new Crosshair());
        x.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        configuration.addxAxis(x);

        y = new YAxis();
        y.setMin(0);
        configuration.addyAxis(y);
    }
}
