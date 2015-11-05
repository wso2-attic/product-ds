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
 * Initializer for the script
 * */
var initBarChart;

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
        var margin = {top: 20, right: 30, bottom: 30, left: 70},
            width = state.width - margin.left - margin.right,
            height = state.height - margin.top - margin.bottom,

            svg, chartGroup, xAxisGroup, yAxisGroup, barGroup, xAxis,
            yAxis, xScale, yScale, color, rectangles, subscribeData,
            head, current, length,

            GENDER_CHANNEL = "gender",
            AGE_CHANNEL = "age",
            STATE_CHANNEL = "state";

        /*
         * Initialize the linked list.
         * @private
         * */
        var initLinkedList = function () {
            head = null;
            current = null;
            length = 0;
        };

        /*
         * Add items to the linked list
         * @private
         * */
        var add = function (dataToAdd) {
            var node = {
                    previous: null,
                    data: dataToAdd,
                    next: null
                },
                next_node;

            if (!head) {
                head = node;
                current = head;
                length++;
            } else {
                next_node = head;
                while (next_node.next) {
                    next_node = next_node.next;
                }
                node.previous = next_node;
                next_node.next = node;
                current = node;
                length++;
            }
        };

        /*
         * Remove the current node from the linked list.
         * @private
         * */
        var removeCurrent = function () {
            var prev_node = current.previous;
            prev_node.next = null;
            current = prev_node;
        };

        /*
         * Get the age details by state.
         * @private
         * */
        var getAgeDetailsByState = function (stateId) {
            var age_data = [];

            /*
             * @type element - element is a object which contain the population
             * information of USA. These object collection can be viewed in file
             * data.js under the USA_DEMOGRAPHICS_SAMPLE_DATA.
             * */
            $.each(USA_DEMOGRAPHICS_SAMPLE_DATA, function (index, element) {
                if (element.id == stateId) {
                    $.extend(true, age_data, element.populationAgeGender);
                    return false;
                }
            });

            return age_data;
        };

        /*
         * Get the name of state by state id.
         * @private
         * */
        var getStateNameByStateId = function(stateId){
            var name = "";

            $.each(USA_DEMOGRAPHICS_SAMPLE_DATA,function(index,element){
               if(element.id == stateId){
                   var history = element.populationHistory[0];
                   name = history.name;
                   return false;
               }
            });

            return name;
        };

        /*
         * Initialize the user interface functionality.
         * @private
         * */
        var initUI = function () {
            state.btnBack.click(backEvent);
            state.btnBack.hide();

            color = createColor();
            xScale = createXScale(USA_DEMOGRAPHICS_SAMPLE_DATA[0].populationAgeGender);
            yScale = createYScale(USA_DEMOGRAPHICS_SAMPLE_DATA[0].populationAgeGender);
            xAxis = createXAxis(xScale);
            yAxis = createYAxis(yScale);

            svg = createSVG();
            chartGroup = createChartGroup();
            xAxisGroup = createXAxisGroup(chartGroup, xAxis);
            yAxisGroup = createYAxisGroup(chartGroup, yAxis);
            barGroup = createBarGroup(chartGroup, USA_DEMOGRAPHICS_SAMPLE_DATA[0].populationAgeGender);

            createBarChart(USA_DEMOGRAPHICS_SAMPLE_DATA[0].populationAgeGender, null, "US", false);

            gadgets.HubSettings.onConnect = function () {
                // Subscribe to the state channel.
                gadgets.Hub.subscribe(STATE_CHANNEL, function (topic, message) {
                    callbackForChannels(message);
                });
            };
        };

        /*
         * Callback function for channels.
         * @private
         * */
        var callbackForChannels = function (message) {
            if (message) {
                subscribeData = message;
                initLinkedList();
                $("#back").hide();
                var ageData = getAgeDetailsByState(message.data);
                createBarChart(ageData, null, message.state, false);
            }
        };

        /*
         * Bind the event for the back button
         * @private
         * */
        var backEvent = function () {
            if (current.previous) {
                var stateData = getAgeDetailsByState(current.data.state);
                createBarChart(stateData, current.data.parent, current.data.state, true);
                removeCurrent();
                if (!current.previous) {
                    $("#back").hide();
                }
            }
        };

        /*
         * Create Color scale.
         * @private
         * */
        var createColor = function () {
            return d3.scale.category20();
        };

        /*
         * Create SVG element for visualization.
         * @private
         * */
        var createSVG = function () {
            return d3.select("body")
                .append("svg:svg")
                .attr("class", "chart")
                .attr("width", width + margin.left + margin.right)
                .attr("height", height + margin.top + margin.bottom);
        };

        /*
         * Create X axis scale.
         * @private
         * */
        var createXScale = function (axisData) {
            return d3.scale.ordinal()
                .domain(axisData.map(function (d) {
                    return d.name;
                }))
                .rangeRoundBands([0, width], .1);
        };

        /*
         * Create Y axis scale.
         * @private
         * */
        var createYScale = function (axisData) {
            return d3.scale.linear()
                .domain([0, d3.max(axisData, function (d) {
                    return d.currentPopulation;
                })])
                .range([height, 0], .1);
        };

        /*
         * Create X axis.
         * @private
         * */
        var createXAxis = function (scale) {
            return d3.svg.axis().scale(scale).orient("bottom");
        };

        /*
         * Create Y axis.
         * @private
         * */
        var createYAxis = function (scale) {
            return d3.svg.axis().scale(scale).orient("left");
        };

        /*
         * Create group for the chart
         * @private
         * */
        var createChartGroup = function () {
            return d3.select(".chart")
                .append("g")
                .attr("class", "chartgroup")
                .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
        };

        /*
         * Create X axis group.
         * @private
         * */
        var createXAxisGroup = function (group, axis) {
            return group.append("g")
                .attr("class", "x axis")
                .attr("transform", "translate(0," + height + ")")
                .call(axis)
                .selectAll("text")
                .style("text-anchor", "end")
                .attr("dx", "-.8em")
                .attr("dy", "-.2em")
                .style("font-size", "10")
                .attr("transform", "rotate(-40)");
        };

        /*
         * Create Y axis group.
         * @private
         * */
        var createYAxisGroup = function (group, axis) {
            return group.append("g")
                .attr("class", "y axis")
                .attr("transform", "translate(" + 0 + "," + 0 + ")")
                .call(axis)
                .append("text")
                .attr("transform", "rotate(-90)")
                .attr("dy", ".71em")
                .attr("y", 6)
                .style("text-anchor", "end")
                .text("population");
        };

        /*
         * Create Bar group.
         * @private
         * */
        var createBarGroup = function (group, barData) {
            return group.append("g")
                .data(barData)
                .attr("transform", function () {
                    return "translate(0," + 0 + ")";
                });
        };

        /*
         * Publish the age details
         * @private
         * */
        var publishAgeDetails = function (dataToSend, age) {
            var dataBundle = {
                data: dataToSend,
                age: age,
                state: subscribeData ? subscribeData.state : "US"
            };

            if (age) {
                // Publish the selected Gender details.
                gadgets.Hub.publish(GENDER_CHANNEL, dataBundle);
            } else {
                // Publish the selected Age details.
                gadgets.Hub.publish(AGE_CHANNEL, dataBundle);
            }
        };

        /*
         * Create Bar chart.
         * @private
         * */
        var createBarChart = function (dataToProcess, parent, stateId, isBack) {
            var path = state.txtPath.text();
            if (isBack) {
                if (parent) {
                    path = path.replace("-> " + parent, "");
                } else {
                    path = getStateNameByStateId(stateId);
                }
            } else {
                if (parent) {
                    path = path + "-> " + parent;
                } else {
                    path = getStateNameByStateId(stateId);
                }
                var newNode = {
                    state: stateId,
                    parent: parent
                };

                add(newNode);
            }

            state.txtPath.text(path);

            svg.select(".axis-label").remove();

            xScale = createXScale(dataToProcess);
            yScale = createYScale(dataToProcess);

            xAxis = createXAxis(xScale);
            yAxis = createYAxis(yScale);

            svg.select(".x.axis")
                .transition()
                .duration(1000)
                .call(xAxis)
                .selectAll("text")
                .style("text-anchor", "end")
                .attr("dx", "-.8em")
                .attr("dy", "-.2em")
                .style("font-size", "10")
                .attr("transform", "rotate(-40)");

            svg.select(".x.axis")
                .append("text")
                .attr("class","axis-label")
                .attr("x",width+10)
                .attr("y",15)
                .style("text-anchor", "end")
                .text("age");

            svg.select(".y.axis")
                .transition()
                .duration(1000)
                .call(yAxis);

            barGroup.selectAll("rect").remove();

            rectangles = barGroup.selectAll(".bar").data(dataToProcess);
            rectangles.enter().append("rect")
                .attr("class", "bar")
                .attr("width", xScale.rangeBand())
                .attr("x", function (d) {
                    return xScale(d.name);
                })
                .attr("fill", function (d) {
                    return color(d.currentPopulation);
                })
                .attr("y", yScale(0))
                .attr("height", 0)
                .transition()
                .duration(1000)
                .attr("height", function (d) {
                    return height - yScale(d.currentPopulation);
                })
                .attr("y", function (d) {
                    return yScale(d.currentPopulation);
                });

            rectangles.on("click", function (d) {
                if (d.category.length > 0) {
                    $("#back").show();
                    createBarChart(d.category, d.name, stateId, false);

                    // Publish Age details.
                    publishAgeDetails(d.name, null);
                } else {
                    // Publish Age details.
                    publishAgeDetails(d.name, d.id);
                }
            })
                .on("mouseover", function () {
                    d3.select(this)
                        .transition()
                        .duration(250)
                        .attr("width", (xScale.rangeBand() + 3))
                        .attr("height", function (d) {
                            return height - yScale(d.currentPopulation);
                        })
                        .attr("y", function (d) {
                            return yScale(d.currentPopulation);
                        });
                })
                .on("mouseout", function () {
                    d3.select(this)
                        .transition()
                        .duration(250)
                        .attr("width", xScale.rangeBand())
                        .attr("height", function (d) {
                            return height - yScale(d.currentPopulation);
                        })
                        .attr("y", function (d) {
                            return yScale(d.currentPopulation);
                        });
                });
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
     * Initialize the bar chart.
     * */
    initBarChart = function (properties) {
        dispose();
        state = properties;
        configure();
    };
}());