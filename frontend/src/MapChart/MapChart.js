import React, { useState, memo } from "react";
import ReactDOM from "react-dom";
import retargetEvents from 'react-shadow-dom-retarget-events';
import ReactTooltip from "react-tooltip";
import {
    ComposableMap,
    Geographies,
    Geography
} from "./react-simple-maps-modified";

const geoUrl =
    "https://raw.githubusercontent.com/zcreativelabs/react-simple-maps/master/topojson-maps/world-110m.json";

const MapChart = () => {
    const [content, setContent] = useState("");
    const [clientX, setClientX] = useState(0);
    const [clientY, setClientY] = useState(0);
    const [tooltipVisible, setTooltipVisible] = useState('hidden');

    const styles = {
        position: 'absolute',
        top: (clientY + 20) + 'px',
        left: (clientX + 20) + 'px',
        width: "120px",
        backgroundColor: "black",
        color: "#fff",
        textAlign: "center",
        borderRadius: "6px",
        padding: "5px 0",
        position: "absolute",
        zIndex: "1",
        visibility: tooltipVisible
    };

    return (
        <>
            <span className="tooltip-span" style={styles}>{content}</span>
            <ComposableMap data-tip="" projectionConfig={{ scale: 200 }}>
                <Geographies geography={geoUrl}>
                    {({ geographies }) =>
                        geographies.map(geo => (
                            <Geography
                                key={geo.rsmKey}
                                geography={geo}
                                onMouseEnter={(e) => {
                                    const { NAME, POP_EST } = geo.properties;
                                    setContent(`${NAME}`);
                                    setClientX(e.clientX);
                                    setClientY(e.clientY);
                                    setTooltipVisible('visible');
                                }}
                                onMouseLeave={() => {
                                    setContent("");
                                    setTooltipVisible('hidden');
                                }}
                                onMouseDown={() => {
                                    console.log(`You choose: ${content}`);
                                }}
                                style={{
                                    default: {
                                        fill: "#D6D6DA",
                                        outline: "none"
                                    },
                                    hover: {
                                        fill: "#F53",
                                        outline: "none"
                                    },
                                    pressed: {
                                        fill: "#E42",
                                        outline: "none"
                                    }
                                }}
                            />
                        ))
                    }
                </Geographies>
            </ComposableMap>
        </>
    );
};

class MapChartComponent extends HTMLElement {
    constructor() {
        super();
        this.mountPoint = document.createElement('div');
        const shadowRoot = this.attachShadow({ mode: 'open' });
        shadowRoot.appendChild(this.mountPoint);

        ReactDOM.render(<MapChart />, this.mountPoint);
        retargetEvents(shadowRoot);
    }
}

window.customElements.define('map-chart-component', MapChartComponent);