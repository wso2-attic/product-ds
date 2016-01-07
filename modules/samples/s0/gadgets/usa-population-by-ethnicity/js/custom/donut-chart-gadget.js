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
 * Initializer for the script.
 * */
var initDonutChart;

/*
 * Immediate function to initialize the page functionality.
 * */
(function () {
    var state = null;

    /*
     * Configure the page functionality
     * */
    var configure = function () {
        var width = state.width,
            height = state.height,
            radios = state.height / 2,
            innerRadios = radios / 2,
            tweenDuration = 250,
            methodsForChangeDonut = [],
            pieData = [],
            oldPieData = [],
            filteredPieData = [],

            donut, color, arc,
            vis, arcGroup, labelGroup, centerGroup, paths,
            whiteCircle, totalLabel, totalValue, totalUnits,

            arraySize,
            streakerDataAdded,
            totalPopulation,
            subscribeData,

            STATE_CHANNEL = "state",
            AGE_CHANNEL = "age",
            ETHNICITY_CHANNEL = "ethnicity";

        var head = null,
            current = null,
            length = 0;

        /*
         * Initialize the linked list.
         * */
        var initLinkedList = function () {
            head = null;
            current = null;
            length = 0;
        };

        /*
         * Add a new node to linked list.
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
         * Remove the current node.
         * */
        var removeCurrent = function () {
            var prev_node = current.previous;
            prev_node.next = null;
            current = prev_node;
        };

        /*
         * Get the state details by state id.
         * */
        var getStateDetailsById = function (stateId) {
            var details = {};

            $.each(USA_DEMOGRAPHICS_SAMPLE_DATA, function (index, element) {
                if (element.id == stateId) {
                    details = element;
                    return false;
                }
            });

            return details;
        };

        /*
         * Get the population data for each state
         * */
        var getPopulationByState = function (stateId) {
            var population_data = [],
                data = getStateDetailsById(stateId);

            $.extend(true, population_data, data.category);
            return population_data;
        };

        /*
         * Get the population by gender
         * */
        var getPopulationByGender = function (genderDetails, age) {
            var gender = [];

            $.each(genderDetails, function (index, element) {
                if (element.name == age) {
                    $.extend(true, gender, element.category);
                    return false;
                }
            });

            return gender;
        };

        /*
         * Initialize the user interface functionalities.
         * */
        var initUI = function () {

            state.btnBack.click(bindBackEvent);
            state.btnBack.hide();

            methodsForChangeDonut[STATE_CHANNEL] = updateChartAccordingToState;
            methodsForChangeDonut[AGE_CHANNEL] = updateChartAccordingToAge;

            adjustDonutContainer();

            donut = createDonut();
            color = createColorScale();
            arc = createAnArc();
            vis = createSvg();

            arcGroup = createArcGroup(vis);
            labelGroup = createLabelGroup(vis);
            centerGroup = createCenterTextGroup(vis);

            paths = createMiddleCircle(arcGroup);

            whiteCircle = createWhiteCircle(centerGroup);
            totalLabel = createTotalLable(centerGroup);
            totalValue = createTotalTrafficValueLabel(centerGroup);
            totalUnits = createUnitsLabel(centerGroup);

            var dataBundle = {
                data: "US",
                state: "US"
            };

            callbackForChannel(STATE_CHANNEL, dataBundle);

            // Initialize the subscriber to listen to the subscribed chanel.
            gadgets.HubSettings.onConnect = function () {
                // Subscribe to the age channel.
                gadgets.Hub.subscribe(AGE_CHANNEL, function (topic, message) {
                    callbackForChannel(AGE_CHANNEL, message);
                });

                // Subscribe to the state channel.
                gadgets.Hub.subscribe(STATE_CHANNEL, function (topic, message) {
                    callbackForChannel(STATE_CHANNEL, message);
                });
            };
        };

        /*
         * Callback for channel subscriber.
         * @private
         * */
        var callbackForChannel = function (channel, message) {
            if (message) {
                // Reference to the subscribe data object.
                subscribeData = message;
                initLinkedList();
                $("#back").hide();
                changeDonut(channel, message);
            }
        };

        /*
         * Adjust the donut container height and width
         * @private
         * */
        var adjustDonutContainer = function () {
            d3.select("#donut")
                .style("width", width)
                .style("height", height);
        };

        /*
         * Update the donut chart according to the incoming message from State channel.
         * @private
         * */
        var updateChartAccordingToState = function (message) {
            var ethnicityData = getPopulationByState(message.data);
            update(ethnicityData, null, null, false);
        };

        /*
         * Update the donut chart according to the incoming message from the Age channel.
         * @private
         * */
        var updateChartAccordingToAge = function (message) {
            var stateDetails = getStateDetailsById(message.state);
            var genderData = getPopulationByGender(stateDetails.populationAgeGender, message.data);
            update(genderData, null, null, false);
        };

        /*
         * Change according to the recieving data.
         * */
        var changeDonut = function (channel, message) {
            methodsForChangeDonut[channel](message);
        };

        /*
         * Handle the back event.
         * */
        var bindBackEvent = function () {
            if (current.previous) {
                update(current.previous, current.previous.data.parentName, current.data.parentName, true);
                removeCurrent();
                if (!current.previous) {
                    $("#back").hide();
                }
            }
        };

        /*
         * Create the donut layout.
         * */
        var createDonut = function () {
            return d3.layout.pie().value(function (d) {
                return d.currentPopulation;
            });
        };

        /*
         * Create a color scale.
         * */
        var createColorScale = function () {

            var getColor = function(d){
                return COLORS[d.name];
            };

            return getColor;
        };

        /*
         * Create an arc.
         * */
        var createAnArc = function () {
            return d3.svg.arc()
                .startAngle(function (d) {
                    return d.startAngle;
                })
                .endAngle(function (d) {
                    return d.endAngle;
                })
                .innerRadius(innerRadios)
                .outerRadius(radios);
        };

        /*
         * Create SVG.
         * */
        var createSvg = function () {
            return d3.select("#donut").append("svg:svg")
                .attr("width", width)
                .attr("height", height);
        };

        /*
         * Create group for arcs.
         * */
        var createArcGroup = function (svg) {
            return svg.append("svg:g")
                .attr("class", "arc")
                .attr("transform", "translate(" + (width / 2) + "," + (height / 2) + ")");
        };

        /*
         * Create group for labels.
         * */
        var createLabelGroup = function (svg) {
            return svg.append("svg:g")
                .attr("class", "labelGroup")
                .attr("transform", "translate(" + (width / 2) + "," + (height / 2) + ")");
        };

        /*
         * Create group for center text.
         * */
        var createCenterTextGroup = function (svg) {
            return svg.append("svg:g")
                .attr("class", "centerGroup")
                .attr("transform", "translate(" + (width / 2) + "," + (height / 2) + ")");
        };

        /*
         * Create the placeholder circle.
         * */
        var createMiddleCircle = function (groupArc) {
            return groupArc.append("svg:circle")
                .attr("fill", "#EFEFEF")
                .attr("r", radios);
        };

        /*
         * Create white circle.
         * */
        var createWhiteCircle = function (groupCenter) {
            return groupCenter.append("svg:circle")
                .attr("fill", "white")
                .attr("r", innerRadios);
        };

        /*
         * Create "Total" Label.
         * */
        var createTotalLable = function (groupCenter) {
            return groupCenter.append("svg:text")
                .attr("class", "label")
                .attr("dy", -15)
                .attr("text-anchor", "middle")
                .style("font-family", "monospace")
                .style("font-size", "12")
                .text("TOTAL");
        };

        /*
         * Create the Population value label.
         * */
        var createTotalTrafficValueLabel = function (groupCenter) {
            return groupCenter.append("svg:text")
                .attr("class", "total")
                .attr("dy", 7)
                .attr("text-anchor", "middle")
                .style("font-family", "monospace")
                .style("font-size", "12")
                .text("Waiting...");
        };

        /*
         * Create "UNITS" label.
         * */
        var createUnitsLabel = function (groupCenter) {
            return groupCenter.append("svg:text")
                .attr("class", "units")
                .attr("dy", 21)
                .attr("text-anchor", "middle")
                .style("font-family", "monospace")
                .style("font-size", "12")
                .text("people");
        };

        /*
         * Filter data for donut
         * */
        var filterData = function (element, index) {
            element.name = streakerDataAdded[index].name;
            element.value = streakerDataAdded[index].currentPopulation;
            totalPopulation += element.value;
            return (element.value > 0);
        };

        /*
         * Draw the pie during a transition.
         * */
        var pieTween = function (d, i) {
            var start;
            var end;

            // If old data is available.
            // Get the start and end angle of previous pie.
            // Get the old pie data to find the to which side we draw.
            if (oldPieData[i]) {
                start = oldPieData[i].startAngle;
                end = oldPieData[i].endAngle;
            } else if (!(oldPieData[i]) && oldPieData[i - 1]) {
                start = oldPieData[i - 1].endAngle;
                end = oldPieData[i - 1].endAngle;
            } else if (!(oldPieData[i - 1]) && oldPieData.length > 0) {
                start = oldPieData[oldPieData.length - 1].endAngle;
                end = oldPieData[oldPieData.length - 1].endAngle;
            } else {
                start = 0;
                end = 0;
            }

            // Interpolate as to the given data.
            var interpolate = d3.interpolate({startAngle: start, endAngle: end}, {
                startAngle: d.startAngle,
                endAngle: d.endAngle
            });
            return function (t) {
                var b = interpolate(t);
                return arc(b);
            };
        };

        /*
         * Update the color detail list.
         * */
        var updateChartDetails = function (data) {
            $("#donutDetails").empty();
            data.forEach(function (element, index) {
                var html = "<li>" + "<div style='background-color:" + color(element) + ";'></div><span>" + element.name + " - " + element.currentPopulation + "</span></li>";
                $("#donutDetails").append(html);
            });
        };

        /*
         * Publish Ethnicity data.
         * */
        var publishEthnicityData = function (ethnicityId) {
            var dataBundle = {
                data: ethnicityId,
                state: subscribeData.state
            };
            gadgets.Hub.publish(ETHNICITY_CHANNEL, dataBundle);
        };

        /*
         * Update donut from the new data.
         * */
        var update = function (dataToProcess, parentName, prevParent, isBack) {

            $("#well").show();
            totalPopulation = 0;

            /* If back operation
             * Replace old data
             * else add new data.
             */
            if (isBack) {
                arraySize = dataToProcess.data.data.length;
                streakerDataAdded = dataToProcess.data.data;

                oldPieData = dataToProcess.data.oldPieData;
                pieData = donut(streakerDataAdded);

                filteredPieData = pieData.filter(filterData);

            } else {
                arraySize = dataToProcess.length;
                streakerDataAdded = dataToProcess;

                oldPieData = filteredPieData;
                pieData = donut(streakerDataAdded);

                filteredPieData = pieData.filter(filterData);

                var linkedListData = {
                    oldPieData: oldPieData,
                    parentName: parentName,
                    data: dataToProcess
                };

                add(linkedListData);
            }

            updateChartDetails(streakerDataAdded);

            if (parentName) {
                var path = state.txtPath.text();
                if (prevParent) {
                    path = path.replace("->" + prevParent, "");
                } else if (!isBack) {
                    path = path + "->" + parentName;
                }

                state.txtPath.text(path);
            } else {
                state.txtPath.text("All");
            }

            arcGroup.selectAll("circle").remove();
            totalValue.text(function () {
                return totalPopulation;
            });

            arcGroup.selectAll("path").remove();
            arcGroup.selectAll("text").remove();

            paths = arcGroup.selectAll("path").data(filteredPieData);
            paths.enter().append("svg:path")
                .attr("stroke", "white")
                .attr("stroke-width", 0.5)
                .attr("fill", function (d, i) {
                    console.log(d);
                    return color(d);
                })
                .transition()
                .duration(tweenDuration)
                .attrTween("d", pieTween);

            // Bind the on click event for each part of the donut
            paths.on("click", function (d) {
                var allPaths = arcGroup.selectAll("path");

                if (d.data.category.length > 0) {
                    allPaths.transition().duration(tweenDuration).attrTween("d", pieTween);
                    $("#back").show();
                    update(d.data.category, d.data.name, null, false);
                }

                if (d.data.name != "Male" && d.data.name != "Female") {
                    publishEthnicityData(d.data.id);
                }
            })
                .on("mouseover", function () {
                    d3.select(this)
                        .transition()
                        .duration(250)
                        .attr("d", d3.svg.arc()
                            .startAngle(function (d) {
                                return d.startAngle;
                            })
                            .endAngle(function (d) {
                                return d.endAngle;
                            })
                            .innerRadius(innerRadios + 2).outerRadius(radios + 2));

                })
                .on("mouseout", function () {
                    d3.select(this)
                        .transition()
                        .duration(250)
                        .attr("d", d3.svg.arc()
                            .startAngle(function (d) {
                                return d.startAngle;
                            })
                            .endAngle(function (d) {
                                return d.endAngle;
                            })
                            .innerRadius(innerRadios).outerRadius(radios));
                });
        };

        initUI();
    };

    /*
     * Dispose the current page state.
     * */
    var dispose = function () {
        state = null;
    };

    /*
     * Initialize Donut chart functionality.
     **/
    initDonutChart = function (properties) {
        dispose();
        state = properties;
        configure();
    };
}());