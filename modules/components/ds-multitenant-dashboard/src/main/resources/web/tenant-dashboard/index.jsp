<!--
 ~ Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 ~
 ~ WSO2 Inc. licenses this file to you under the Apache License,
 ~ Version 2.0 (the "License"); you may not use this file except
 ~ in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~    http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing,
 ~ software distributed under the License is distributed on an
 ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~ KIND, either express or implied.  See the License for the
 ~ specific language governing permissions and limitations
 ~ under the License.
 -->
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>
<jsp:include page="../dialog/display_messages.jsp"/>

<link href="../tenant-dashboard/css/dashboard-common.css" rel="stylesheet" type="text/css" media="all"/>
<%
        Object param = session.getAttribute("authenticated");
        String passwordExpires = (String) session.getAttribute(ServerConstants.PASSWORD_EXPIRATION);
        boolean loggedIn = false;
        if (param != null) {
            loggedIn = (Boolean) param;             
        } 
%>
  
<div id="passwordExpire">
         <%
         if (loggedIn && passwordExpires != null) {
         %>
              <div class="info-box"><p>Your password expires at <%=passwordExpires%>. Please change by visiting <a href="../user/change-passwd.jsp?isUserChange=true&returnPath=../admin/index.jsp">here</a></p></div>
         <%
             }
         %>
</div>
<div id="middle">
<div id="workArea">


<style type="text/css">
    .tip-table td.create-dashboard {
        background-image: url(../../carbon/tenant-dashboard/images/create-dashboard2.png);
    }

    .tip-table td.analytics {
        background-image: url(../../carbon/tenant-dashboard/images/analytics.png);
    }

    .tip-table td.personalize-dashboard {
        background-image: url(../../carbon/tenant-dashboard/images/personalize1.png);
    }
    .tip-table td.secure-dashboard {
        background-image: url(../../carbon/tenant-dashboard/images/security1.png);
    }
</style>
 <h2 class="dashboard-title">WSO2 Dashoard Server Quick Start Dashboard</h2>
        <table class="tip-table">
            <tr>
                <td class="tip-top create-dashboard"></td>
                <td class="tip-empty"></td>
                <td class="tip-top analytics"></td>
                <td class="tip-empty"></td>
                <td class="tip-top personalize-dashboard"></td>
                <td class="tip-empty "></td>
                <td class="tip-top secure-dashboard"></td>
            </tr>
            <tr>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                        <h3 class="tip-title">Create Your Own Dashboard </h3> <br/>
                        <p align="justify">Create your own dashboard with any amount of pages you would like to expose in the dashboard to meet your business requirement.
                            You can create your gadgets which can connect with the systems that you have data,
                            and then plug that in the dashboard and enable different types of users to visualize it.
                            <a href="https://docs.wso2.com/display/DS200/Creating+a+Dashboard"> More..</a>
                        </p>
                    </div>
                </td>
                <td class="tip-empty"></td>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                        <h3 class="tip-title">Integrate with Data Analytics</h3> <br/>
                        <p align="justify">You can integrate the dashboards, and create the gadgets to visualize your analytics results.
                            In the current world, you need to analyze big data which was collected from various places,
                            and you can use WSO2 Dashboard Server to visualize the results.
                            <a href="https://docs.wso2.com/display/DS200/Creating+a+Gadget"> More..</a>
                        </p>
                    </div>
                </td>
                <td class="tip-empty"></td>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                         <h3 class="tip-title">Personalize Your Dashboard</h3><br/>
                        <p align="justify">Every users have their own interests and would like to see the view of the dashboard in meeting their expectations.
                            Therefore the dashboards created can be customized and
                            re-arranged in the way the current user would like to view it without affecting the other users view.
                            <a href="https://docs.wso2.com/display/DS200/Personalizing+a+Viewer+Dashboard"> More..</a>
                        </p>

                    </div>
                </td>
                <td class="tip-empty"></td>
                <td class="tip-content">
                    <div class="tip-content-lifter">
                        <h3 class="tip-title">Secure Your Dashboard</h3> <br/>
                        <p align="justify">Securing your dashboard is very important aspect,
                            and you would like to show the content only to the relevant and authorized users in your organization.
                            WSO2 Dashboard Server facilitates this, and also further provides you the way to connect the dashboards
                            with the Identity Provider which already existing in your organization.
                            <a href="https://docs.wso2.com/display/DS200/Securing+a+Dashboard"> More..</a>
                        </p>
                    </div>
                </td>
            </tr>
            <tr>
                <td class="tip-bottom"></td>
                <td class="tip-empty"></td>
                <td class="tip-bottom"></td>
                <td class="tip-empty"></td>
                <td class="tip-bottom"></td>
                <td class="tip-empty"></td>
                <td class="tip-bottom"></td>
            </tr>
        </table>
<p>
    <br/>
</p> </div>
</div>

