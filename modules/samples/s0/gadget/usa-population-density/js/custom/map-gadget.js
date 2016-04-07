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
var initMap;

/*
 * Immediate function to initialize the page functionality.
 * */
(function () {
    var state = null;

    /*
     * Configure the page functionality.
     * @private
     */
    var configure = function () {
        var width = state.width,
            height = state.height,
            centered,

            projection, path, color, stateGroup,
            densityColors = DENSITY_COLORS.stateDensityColors,
            previousColors = [],
            methodsForDensityColors = [],
            colorsForEachDensities = [],

            countryGeoData,
            subscribeData,

            ETHNICITY_CHANNEL = "ethnicity",
            AGE_CHANNEL = "age",
            GENDER_CHANNEL = "gender",
            HISTORY_CHANNEL = "history",
            STATE_CHANNEL = "state";
        
        var currentState;

        /*
         * Get the state details by id.
         * @private
         */
        var getStateDetailsById = function (stateId) {
            var details = {};

            /*
             * @type element - element is a object which contain the population
             * information of USA. These object collection can be viewed in file
             * data.js under the USA_DEMOGRAPHICS_SAMPLE_DATA.
             * */
            $.each(USA_DEMOGRAPHICS_SAMPLE_DATA, function (index, element) {
                if (element.id == stateId) {
                    details = element;
                    return false;
                }
            });

            return details;
        };

        /*
         * Get the population by Ethnicity in selected state.
         * @private
         */
        var getPopulationByEthnicityID = function (ethnicities, ethnicityId) {
            var currentPopulation = 0;

            /*
             * @type element - element is a object which contain the ethnicity
             * information of USA. These object collection can be viewed in file
             * data.js under the USA_DEMOGRAPHICS_SAMPLE_DATA in category array
              * in each object.
             * */
            $.each(ethnicities, function (index, element) {
                if (element.id == ethnicityId) {
                    currentPopulation = element.currentPopulation;
                    return false;
                } else if (element.category.length > 0) {
                    // Call the method recursively.
                    currentPopulation = getPopulationByEthnicityID(element.category, ethnicityId);
                    if (currentPopulation > 0) {
                        return false;
                    }
                }
            });

            return currentPopulation;
        };

        /*
         * Get the population by the Gender.
         * @private
         */
        var getPopulationByGender = function (genders, genderId, age) {
            var currentPopulation = 0;

            /*
             * @type element - element is a object which contain the gender
             * information of USA. These object collection can be viewed in file
             * data.js under the USA_DEMOGRAPHICS_SAMPLE_DATA in populationAgeGender
             * array.
             * */
            $.each(genders, function (index, element) {
                if (element.id == age && element.name == genderId) {
                    currentPopulation = element.currentPopulation;
                    return false;
                } else if (element.category.length > 0) {
                    // Call the method recursively.
                    currentPopulation = getPopulationByGender(element.category, genderId, age);
                    if (currentPopulation > 0) {
                        return false;
                    }
                }
            });

            return currentPopulation;
        };

        /*
         * Get the population by Age given in selected state.
         * @private
         */
        var getPopulationByAge = function (ageGenderDetails, ageId) {
            var currentPopulation = 0;

            /*
             * @type element - element is a object which contain the age
             * information of USA. These object collection can be viewed in file
             * data.js under the USA_DEMOGRAPHICS_SAMPLE_DATA in populationAgeGender
             * array.
             * */
            $.each(ageGenderDetails, function (index, element) {
                if (element.name == ageId) {
                    currentPopulation = element.currentPopulation;
                    return false;
                }
            });

            return currentPopulation;
        };

        /*
         * Get the population by year.
         * @private
         */
        var getPopulationByYear = function (historyDetails, year) {
            var currentPopulation = 0;

            /*
             * @type element - element is a object which contain the population history
             * information of USA. These object collection can be viewed in file
             * data.js under the USA_DEMOGRAPHICS_SAMPLE_DATA in populationHistory
             * array.
             * */
            $.each(historyDetails, function (index, element) {
                if (element.year == year) {
                    currentPopulation = element.currentPopulation;
                    return false;
                }
            });

            return currentPopulation;
        };

        /*
         * Get the current population by state id.
         * @private
         */
        var getCurrentPopulationById = function (stateId) {
            var currentPopulation,
                data = getStateDetailsById(stateId);

            currentPopulation = data.currentPopulation;

            return currentPopulation;
        };

        /*
         * Initialize the User Interface functionality and styles.
		 * @return {null}
         * @private
         */
        var initUI = function () {
            methodsForDensityColors[ETHNICITY_CHANNEL] = getColorForEthnicityDensity;
            methodsForDensityColors[AGE_CHANNEL] = getColorForAgeDensity;
            methodsForDensityColors[GENDER_CHANNEL] = getColorForGenderDensity;
            methodsForDensityColors[HISTORY_CHANNEL] = getColorForHistoryDensity;
            methodsForDensityColors[STATE_CHANNEL] = getColorForPopulationDensity;

            colorsForEachDensities[ETHNICITY_CHANNEL] = DENSITY_COLORS.ethnicityDensityColors;
            colorsForEachDensities[AGE_CHANNEL] = DENSITY_COLORS.ageDensityColors;
            colorsForEachDensities[GENDER_CHANNEL] = DENSITY_COLORS.genderDensityColors;
            colorsForEachDensities[HISTORY_CHANNEL] = DENSITY_COLORS.historyDensityColors;
            colorsForEachDensities[STATE_CHANNEL] = DENSITY_COLORS.stateDensityColors;

            adjustMapContainer();
            var svg = createSVG();
            projection = createProjection();
            path = createGeoPath();
            createMap(svg);
            appendKeyScale();
            
            // restore gadget state
            wso2.gadgets.state.getGadgetState(function(gadgetState) {
                gadgetState = gadgetState || { };
                gadgetState.state = gadgetState.state ? gadgetState.state.toUpperCase() : 'US';
                
                if (gadgetState.ethnicity) {
                    callbackForChannel(ETHNICITY_CHANNEL, 
                        { state: gadgetState.state, ethnicity: gadgetState.ethnicity });
                    return;
                }
                
                if (gadgetState.age) {
                    callbackForChannel(AGE_CHANNEL, { state: gadgetState.state, age: gadgetState.age });
                    return;
                }
                
                if (gadgetState.gender) {
                    callbackForChannel(GENDER_CHANNEL, { state: gadgetState.state, gender: gadgetState.gender });
                    return;
                }
                
                if (gadgetState.year) {
                    callbackForChannel(HISTORY_CHANNEL, { state: gadgetState.state, year: gadgetState.year });
                    return;
                }
                
                // zoom in the selected state
                if (gadgetState.state != 'US') {
                    var paths = d3.selectAll('path')[0];
                    for(var i = 0; i < paths.length; i++) {
                        if (paths[i].__data__.id == gadgetState.state) {
                            onStateClick(paths[i].__data__, svg, true);
                            break;
                        }
                    }
                }
            });
            
            gadgets.HubSettings.onConnect = function () {
                // Subscribe to Ethnicity channel
                gadgets.Hub.subscribe(ETHNICITY_CHANNEL, function (topic, message) {
                    updateGadgetState({ethnicity: message.ethnicity});
                    callbackForChannel(ETHNICITY_CHANNEL, message);
                });

                // Subscribe to Age channel
                gadgets.Hub.subscribe(AGE_CHANNEL, function (topic, message) {
                    updateGadgetState({age: message.age});
                    callbackForChannel(AGE_CHANNEL, message);
                });

                // Subscribe to gender channel
                gadgets.Hub.subscribe(GENDER_CHANNEL, function (topic, message) {
                    updateGadgetState({gender: message.gender});
                    callbackForChannel(GENDER_CHANNEL, message);
                });

                // Subscribe to History channel
                gadgets.Hub.subscribe(HISTORY_CHANNEL, function (topic, message) {
                    updateGadgetState({year: message.year});
                    callbackForChannel(HISTORY_CHANNEL, message);
                });
            };
        };
        
        /**
         * Update gadget state.
         * @param {Object} s Gadget state
         * @return {null}
         * @private
         */
        var updateGadgetState = function(s) {
            wso2.gadgets.state.getGadgetState(function(gadgetState) {
                gadgetState = gadgetState || { }
                gadgetState.state = gadgetState.state || 'US';
                var newState = { 
					state: s.state || gadgetState.state
				};
                
                if (s.ethnicity) {
                    newState.ethnicity = s.ethnicity;
                } else if (s.age) {
                    newState.age = s.age;
                } else if (s.gender) {
                    newState.gender = s.gender;
                } else if (s.year) {
                    newState.year = s.year;
                }
                wso2.gadgets.state.setGadgetState(newState);
            });
        }
        
        /*
         * Callback of channel subscriber.
		 * @param {String} channel Received channel
		 * @param {Object} message Message received
		 * @return {null}
         * @private
         */
        var callbackForChannel = function (channel, message) {
            if (message) {
                subscribeData = message;
                previousColors = [];

                if (channel == AGE_CHANNEL) {
                    changeColorByDensity(channel, message.age);
                } else if (channel == GENDER_CHANNEL) {
                    changeColorByDensity(channel, message.gender);
                } else if (channel == ETHNICITY_CHANNEL) {
                    changeColorByDensity(channel, message.ethnicity);
                } else if (channel == HISTORY_CHANNEL) {
                    changeColorByDensity(channel, message.year);
                }
                zoomOutMap();
            }
        };

        /*
         * Get color according to population distribution density as to ethnicity.
         * @private
         */
        var getColorForEthnicityDensity = function (stateId, ethnicityId) {
            var stateDetails, currentPopulation;
            stateDetails = getStateDetailsById(stateId);
            currentPopulation = getPopulationByEthnicityID(stateDetails.category, ethnicityId);
            previousColors[stateId] = getColorByPopulation(currentPopulation);
            return getColorByPopulation(currentPopulation);
        };

        /*
         * Get color according to population distribution density as to age.
         * @private
         */
        var getColorForAgeDensity = function (stateId, ageId) {
            var stateDetails, currentPopulation;
            stateDetails = getStateDetailsById(stateId);
            currentPopulation = getPopulationByAge(stateDetails.populationAgeGender, ageId);
            previousColors[stateId] = getColorByPopulation(currentPopulation);
            return getColorByPopulation(currentPopulation);
        };

        /*
         * Get color according to population distribution density as to gender.
         * @private
         */
        var getColorForGenderDensity = function (stateId, genderId) {
            var stateDetails, currentPopulation;
            stateDetails = getStateDetailsById(stateId);
            currentPopulation = getPopulationByGender(stateDetails.populationAgeGender, genderId, subscribeData.age);
            previousColors[stateId] = getColorByPopulation(currentPopulation);
            return getColorByPopulation(currentPopulation);
        };

        /*
         * Get the color according to population distribution density as to year.
         * @private
         */
        var getColorForHistoryDensity = function (stateId, year) {
            var stateDetails, currentPopulation;
            stateDetails = getStateDetailsById(stateId);
            currentPopulation = getPopulationByYear(stateDetails.populationHistory, year);
            previousColors[stateId] = getColorByPopulation(currentPopulation);
            return getColorByPopulation(currentPopulation);
        };

        /*
         * Get color according to the population density of given state.
         * @private
         */
        var getColorForPopulationDensity = function (stateId) {
            var currentPopulation;
            currentPopulation = getCurrentPopulationById(stateId);
            previousColors[stateId] = getColorByPopulation(currentPopulation);
            return getColorByPopulation(currentPopulation);
        };

        /*
         * Create SVG element.
         * @private
         */
        var createSVG = function () {
            return d3.select("#container").append("svg")
                .attr("width", width)
                .attr("height", height);
        };

        /*
         * Create a Projection.
         * @private
         */
        var createProjection = function () {
            return d3.geo.albersUsa()
                .scale(width-150)
                .translate([width / 2, height / 2]);
        };

        /*
         * Adjust the map container height and width.
         * @private
         */
        var adjustMapContainer = function () {
            d3.select("#container")
                .style("width", width)
                .style("height", height);
        };

        /*
         * Create a GeoPath.
         * @private
         */
        var createGeoPath = function () {
            return d3.geo.path()
                .projection(projection);
        };

        /*
         * Change the color of each state as to the population.
         * @private
         */
        var changeColorByDensity = function (channel, id) {
            d3.select("#states")
                .selectAll("path")
                .transition()
                .duration(500)
                .attr("fill", function (d) {
                    selectColorByDataType(channel);
                    appendKeyScale();
                    return methodsForDensityColors[channel](d.id, id);
                });
        };

        /*
         * Change color according to the population density.
         * @private
         */
        var getColorByPopulation = function (population) {
            if (population <= 10000) {
                return densityColors[0];
            } else if (population > 10000 && population <= 100000) {
                return densityColors[1];
            } else if (population > 100000 && population <= 1000000) {
                return densityColors[2];
            } else if (population > 1000000 && population <= 10000000) {
                return densityColors[3];
            } else if (population > 10000000 && population <= 100000000) {
                return densityColors[4];
            } else if (population > 100000000 && population <= 1000000000) {
                return densityColors[5];
            }
        };

        /*
         * Select the density colors by type.
         * @private
         */
        var selectColorByDataType = function (channel) {
            densityColors = colorsForEachDensities[channel];
        };

        /*
         * Append the color key scale below the map.
         * @private
         */
        var appendKeyScale = function () {
            $("#map-keys").empty();
            $.each(densityColors, function (i, d) {
                $("#map-keys").append('<li><div style="background-color:' + d + ';"></div><span>' + DENSITY_SCALE[i] + '</span></li>');
            });
        };

        /*
         * Clicked event on a state of usa.
         * @param {Object} d D3 selected context
         * @param {Object} svg SVG
         * @param {Boolean} noPublish Flag to indicate no publish via pubsub or gadget state
         * @return {null}
         * @private
         */
        var onStateClick = function (d, svg, noPublish) {
            noPublish = noPublish || false;
            var x, y, scale;

            selectColorByDataType(STATE_CHANNEL);
            changeColorByDensity(STATE_CHANNEL, "");
            appendKeyScale();

            if (d && centered !== d) {
                var centroid = path.centroid(d);
                x = centroid[0];
                y = centroid[1];
                scale = 4;
                centered = d;
                currentState = d.id
                
            } else {
                x = width / 2;
                y = height / 2;
                scale = 1;
                centered = null;

                d3.select(svg[0][0].parentNode.parentNode).select('.datamaps-hoverover').style('display', 'none');
                currentState = 'US'
            }
            
            if (!noPublish) {
                updateGadgetState({state: currentState});
                publishStateData(currentState);
            }

            // Change the path classes back.
            stateGroup.selectAll("path")
                .classed("active", centered && function (d) {
                    return d === centered;
                });

            // Reset the text values.
            stateGroup.selectAll("text").data(countryGeoData).classed("active", centered && function (d) {
                    return d === centered;
                })
                .text(function (d) {
                    return d.id;
                });

            // Animate the transition of zoom in or Zoom out.
            stateGroup.transition()
                .duration(750)
                .attr("transform", "translate(" + width / 2 + "," + height / 2 + ")scale(" + scale + ")translate(" + -x + "," + -y + ")")
                .style("stroke-width", 1.5 / scale + "px");
        };

        /*
         * Zoom out the map.
		 * @return {null}
         * @private
         */
        var zoomOutMap = function () {
            var scale = 1;
            centered = null;
            stateGroup.transition()
                .duration(750)
                .attr("transform", "translate(" + width / 2 + "," + height / 2 + ")scale(" + 1 + ")translate(" + -(width / 2) + "," + -(height / 2) + ")")
                .style("stroke-width", 1.5 / scale + "px");
        };

        /*
         *On mouse is over an state of usa.
         */
        var onMouseOver = function (d, element, svg) {
            $(element).attr("fill", "#FC8D59");
            var position = d3.mouse(element);
            d3.select(svg[0][0].parentNode.parentNode).select(".datamaps-hoverover").style('top', ((position[1] + 30)) + "px").style("opacity", 0)
                .html("<div>" + d.properties.name + "</div>")
                .style('left', (position[0]) + "px");
            d3.select(svg[0][0].parentNode.parentNode).select('.datamaps-hoverover').transition().duration(500).style("opacity", .9).style('display', 'block');
        };

        /*
         *On mouse is out from a state of usa.
         */
        var onMouseOut = function (d, element, svg) {
            $(element).attr("fill", previousColors[d.id]);
            d3.select(svg[0][0].parentNode.parentNode).select('.datamaps-hoverover').transition().duration(200).style("opacity", 0).style('display', 'none');
        };

        /*
         * Create Labels to add to the map.
         * @private
         */
        var addLabels = function (svg) {
            var startPoint = projection([-67.707617, 42.722131]),
                xOffset = 7.5,
                yOffset = 5;

            svg.select("#states").selectAll("path").attr("data-foo", function (d) {
                var center = path.centroid(d);
                if (["FL", "KY", "MI"].indexOf(d.id) > -1) xOffset = 2.5;
                if (d.id === "NY") xOffset = -1;
                if (d.id === "MI") yOffset = 18;
                if (d.id === "LA") xOffset = 13;
                var x, y,
                    stateToBeAligned = ["VT", "NH", "MA", "RI", "CT", "NJ", "DE", "MD", "DC"].indexOf(d.id);

                x = center[0] - xOffset;
                y = center[1];

                if (stateToBeAligned > -1) {
                    var yStart = startPoint[1];
                    x = startPoint[0];
                    y = yStart + (stateToBeAligned * (2 + 12));
                    stateGroup.append("line")
                        .attr("x1", x - 3)
                        .attr("y1", y - 5)
                        .attr("x2", center[0])
                        .attr("y2", center[1])
                        .style("stroke", "#000")
                        .style("stroke-width", 1);
                }

                stateGroup.append("text")
                    .attr("x", x)
                    .attr("y", y)
                    .style("font-family", "monospace")
                    .style("font-size", "" + width / 100)
                    .text(d.id);
            });
        };

        /*
         * Create the map.
         * @private
         */
        var createMap = function (svg) {
            var us = USA_GEO_DATA;

            svg.append("rect")
                .attr("class", "background")
                .attr("width", width)
                .attr("height", height)
                .on("click", function (d) {
                    onStateClick(d, svg);
                });

            stateGroup = svg.append("g");

            // Get the usa geo data using topojson library.
            countryGeoData = topojson.feature(us, us.objects.usa).features;

            stateGroup.append("g")
                .attr("id", "states")
                .selectAll("path")
                .data(topojson.feature(us, us.objects.usa).features)
                .enter().append("path")
                .attr("d", path)
                .attr("fill", function (d) {
                    var currentPopulation = getCurrentPopulationById(d.id);
                    previousColors[d.id] = getColorByPopulation(currentPopulation);
                    return getColorByPopulation(currentPopulation);
                })
                .on("click", function (d) {
                    onStateClick(d, svg);
                })
                .on("mouseover", function (d) {
                    onMouseOver(d, this, svg);
                })
                .on("mouseout", function (d) {
                    onMouseOut(d, this, svg);
                });

            stateGroup.append("path")
                .datum(topojson.mesh(us, us.objects.usa, function (a, b) {
                    return a !== b;
                }))
                .attr("id", "state-borders")
                .attr("d", path);

            addLabels(svg);
        };

        /*
         * Publish State data.
         * @private
         */
        var publishStateData = function (stateId) {
            var dataBundle = {
                state: stateId
            };
            gadgets.Hub.publish(STATE_CHANNEL, dataBundle);
        };

        initUI();
    };

    /*
     * Dispose the page state.
     * @private
     */
    var dispose = function () {
        state = null;
    };

    /*
     * Initialize the page functionality.
     */
    initMap = function (properties) {
        dispose();
        state = properties;
        configure();
    };
}());

