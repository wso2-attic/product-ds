/**
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Initializer for the line chart.
 * */
var initLineChart = null;

/*
 * Immediate function to initialize the page functionality.
 * */
(function () {
    var state = null;

    /*
     * Configure the page functionality.
     * @private
     * */
    var configure = function () {

        var margin = {top: 20, right: 20, bottom: 30, left: 100},
            width = state.width - margin.left - margin.right,
            height = state.height - margin.top - margin.bottom,

            svg, xScale, yScale, xAxis, yAxis,
            xGroup, yGroup, line, tooltip,
            subscribeData,

            STATE_CHANNEL = "state",
            HISTORY_CHANNEL = "history";

        /*
         * Get the population history by state.
         * @private
         * */
        var getPopulationHistoryByState = function (stateId) {
            var populationHistory = [];

            /*
             * @type element - element is a object which contain the population
             * information of USA. These object collection can be viewed in file
             * data.js under the USA_DEMOGRAPHICS_SAMPLE_DATA.
             * */
            $.each(USA_DEMOGRAPHICS_SAMPLE_DATA, function (index, element) {
                if (element.id == stateId) {
                    $.extend(true, populationHistory, element.populationHistory);
                    return false;
                }
            });

            return populationHistory;
        };

        /*
         * Initialize the user interface functionality.
         * @private
         * */
        var initUI = function () {
            xScale = createXScale();
            yScale = createYScale();
            xAxis = createXAxis(xScale);
            yAxis = createYAxis(yScale);
            svg = createSVG();
            line = createALine(xScale,yScale);
            tooltip = createToolTip();

            // Initialize the subscriber to listen to the subscribed channel.
            gadgets.HubSettings.onConnect = function () {
                // Subscribe to the state channel
                gadgets.Hub.subscribe(STATE_CHANNEL, function (topic, message) {
                    callbackForSubscribers(message);
                });
            };
        };

        /*
         * Callback for the subscriber.
         * @private
         * */
        var callbackForSubscribers = function (message) {
            if (message) {
                subscribeData = message;
                var populationHistoryData = getPopulationHistoryByState(message.data);
                update(populationHistoryData);
            }
        };

        /*
         * Create a scale for X axis.
         * @private
         * */
        var createXScale = function () {
            return d3.time.scale()
                .range([0, width]);
        };

        /*
         * Create a scale for Y axis.
         * @private
         * */
        var createYScale = function () {
            return d3.scale.linear()
                .range([height, 0]);
        };

        /*
         * Create X axis for the bar chart.
         * @private
         * */
        var createXAxis = function (scale) {
            return d3.svg.axis()
                .scale(scale)
                .orient("bottom");
        };

        /*
         * Create Y axis for the bar chart.
         * @private
         * */
        var createYAxis = function (scale) {
            return d3.svg.axis()
                .scale(scale)
                .orient("left");
        };

        /*
         * Create the svg.
         * @private
         * */
        var createSVG = function () {
            return d3.select("#linechart").append("svg:svg")
                .attr("width", width + margin.left + margin.right)
                .attr("height", height + margin.top + margin.bottom)
                .append("svg:g")
                .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
        };

        /*
         * Create the line.
         * @private
         * */
        var createALine = function (scaleX,scaleY) {
            return d3.svg.line()
                .x(function (d) {
                    return scaleX(d.year);
                })
                .y(function (d) {
                    return scaleY(d.currentPopulation);
                });
        };

        /*
         * Create xAxis group.
         * @private
         * */
        var createXAxisGroup = function (vis) {
            return vis.append("svg:g")
                .attr("class", "x axis")
                .attr("transform", "translate(0," + height + ")")
                .call(xAxis)
                .selectAll("text")
                .style("text-anchor", "end")
                .attr("dx", "-.8em")
                .attr("dy", ".15em")
                .attr("transform", "rotate(-65)");
        };

        /*
         * Create yAxis Group.
         * @private
         * */
        var createYAxisGroup = function (vis) {
            return vis.append("svg:g")
                .attr("class", "y axis")
                .call(yAxis).append("text")
                .attr("transform", "rotate(-90)")
                .attr("y", 6)
                .attr("dy", ".71em").style("text-anchor", "end")
                .text("population");
        };

        /*
         * Create tool tip.
         * @private
         * */
        var createToolTip = function () {
            return d3.select("#linechart")
                .append("div")
                .attr("class", "tooltip")
                .style("opacity", 0);
        };

        /*
         * Publish history data.
         * @private
         * */
        var publishHistoryData = function (year) {
            var dataBundle = {
                data: year,
                state: subscribeData.state
            };

            // Publish the selected history details.
            gadgets.Hub.publish(HISTORY_CHANNEL, dataBundle);
        };

        /*
         * Update the data chart.
         * @private
         * */
        var update = function (dataToProcess) {
            var parseData = d3.time.format("%Y").parse;

            dataToProcess.forEach(function (element) {
                element.year = parseData("" + element.year);
            });

            d3.select("svg").remove();
            svg = createSVG();
            line = createALine(xScale,yScale);

            xScale.domain(d3.extent(dataToProcess, function (d) {
                return d.year;
            }));
            yScale.domain(d3.extent(dataToProcess, function (d) {
                return d.currentPopulation;
            }));

            xAxis = createXAxis(xScale);
            yAxis = createYAxis(yScale);

            xGroup = createXAxisGroup(svg);
            yGroup = createYAxisGroup(svg);

            d3.select(".x.axis").append("text")
                .attr("x",width)
                .attr("y",-2)
                .style("text-anchor", "end")
                .text("Year");

            var path = svg.append("path")
                .datum(dataToProcess)
                .attr("class", "line")
                .attr("d", line);

            var circle = svg.selectAll("circle").data(dataToProcess)
                .enter()
                .append("circle")
                .attr("stroke", "black")
                .attr("stroke-width", 0.5)
                .attr("fill", "white").attr("fill-opacity", .5)
                .attr("cx", function (d) {
                    return xScale(d.year);
                })
                .attr("cy", function (d) {
                    return yScale(d.currentPopulation);
                })
                .attr("r", 0)
                .transition()
                .duration(250)
                .attr("r", 5);

            svg.selectAll("circle")
                .on("mouseover", function (d) {
                    d3.select(this)
                        .attr("r", 5)
                        .transition()
                        .duration(200)
                        .attr("r", 10);

                    tooltip.transition()
                        .duration(200)
                        .style("opacity", .9);
                    tooltip.html("Year: " + d.year.getFullYear() + "<br/>" + "Pop: " + this.__data__.currentPopulation)
                        .style("left", function () {
                            if ((d3.event.pageX + 100) > state.width) {
                                return (d3.event.pageX - 100) + "px";
                            } else {
                                return (d3.event.pageX) + "px";
                            }
                        })
                        .style("top", function () {
                            if ((d3.event.pageX + 100) > state.width) {
                                return (d3.event.pageY) + "px";
                            } else {
                                return (d3.event.pageY - 28) + "px";
                            }
                        });
                })
                .on("mouseout", function () {
                    d3.select(this)
                        .attr("r", 10)
                        .transition()
                        .duration(200)
                        .attr("r", 5);

                    tooltip.transition()
                        .duration(200)
                        .style("opacity", 0);
                })
                .on("click", function (d) {
                    publishHistoryData(d.year.getFullYear());
                });

            // Get the path length.
            var totalLength = path.node().getTotalLength();

            // Animate the drawing of the path.
            path
                .attr("stroke-dasharray", totalLength + " " + totalLength)
                .attr("stroke-dashoffset", totalLength)
                .transition()
                .duration(500)
                .ease("linear")
                .attr("stroke-dashoffset", 0);
        };

        initUI();
    };

    /*
     * Dispose the current page state.
     * @private
     * */
    var dispose = function () {
        state = null;
    };

    /*
     * Initialize the script for page.
     * */
    initLineChart = function (properties) {
        dispose();
        state = properties;
        configure();
    };
}());
