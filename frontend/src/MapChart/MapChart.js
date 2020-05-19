import React, { useState, memo } from "react";
import ReactTooltip from "react-tooltip";
import {
    ComposableMap,
    Geographies,
    Geography
} from "../react-simple-maps-edited";

const geoUrl =
    "https://raw.githubusercontent.com/zcreativelabs/react-simple-maps/master/topojson-maps/world-110m.json";

const rounded = num => {
    if (num > 1000000000) {
        return Math.round(num / 100000000) / 10 + "Bn";
    } else if (num > 1000000) {
        return Math.round(num / 100000) / 10 + "M";
    } else {
        return Math.round(num / 100) / 10 + "K";
    }
};

const MapChart = () => {
    const [content, setContent] = useState("");
    return (
        <>
            <ReactTooltip>{content}</ReactTooltip>
            <ComposableMap data-tip="" projectionConfig={{ scale: 200 }}>
                <Geographies geography={geoUrl}>
                    {({ geographies }) =>
                        geographies.map(geo => (
                            <Geography
                                key={geo.rsmKey}
                                geography={geo}
                                onMouseEnter={() => {
                                    const { NAME, POP_EST } = geo.properties;
                                    console.log(geo);
                                    setContent(`${NAME} — ${rounded(POP_EST)}`);
                                }}
                                onMouseLeave={() => {
                                    setContent("");
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

export default memo(MapChart);
