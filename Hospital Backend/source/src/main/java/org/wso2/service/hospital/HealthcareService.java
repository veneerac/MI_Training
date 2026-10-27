/*
 * Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.wso2.service.hospital;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.msf4j.Microservice;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Healthcare back-end used by the MI integration tutorials (routing, transformation,
 * service orchestration, templates, store and forward, JMS).
 */
@Path("/")
public class HealthcareService implements Microservice {

    private static final String DEFAULT_AVAILABILITY = "9.00 a.m - 11.00 a.m";
    private static final double DEFAULT_FEE = 5000.0;
    private static final int PAYMENT_DISCOUNT = 20;

    // hospital id -> hospital name
    private final Map<String, String> hospitals = new ConcurrentHashMap<>();
    // hospital id -> (doctor name -> doctor)
    private final Map<String, Map<String, JSONObject>> doctors = new ConcurrentHashMap<>();
    private final Map<Integer, JSONObject> appointments = new ConcurrentHashMap<>();
    private final AtomicInteger appointmentCounter = new AtomicInteger();

    public HealthcareService() {
        hospitals.put("grandoaks", "grand oak community hospital");
        hospitals.put("clemency", "clemency medical center");
        hospitals.put("pinevalley", "pine valley community hospital");
        hospitals.put("willowgardens", "willow gardens general hospital");
        for (String hospitalId : hospitals.keySet()) {
            doctors.put(hospitalId, new ConcurrentHashMap<>());
        }

        addDoctor("grandoaks", "thomas collins", "surgery", DEFAULT_AVAILABILITY, 7000.0);
        addDoctor("grandoaks", "henry parker", "ent", "4.30 p.m - 6.30 p.m", 4500.0);
        addDoctor("grandoaks", "abner jones", "gynaecology", "8.00 a.m - 10.00 a.m", 8000.0);
        addDoctor("grandoaks", "abner jones", "ent", "8.00 a.m - 10.00 a.m", 8000.0);
        addDoctor("clemency", "anne clement", "surgery", "8.00 a.m - 10.00 a.m", 12000.0);
        addDoctor("clemency", "thomas kirk", "gynaecology", DEFAULT_AVAILABILITY, 8000.0);
        addDoctor("clemency", "cailen cooper", "paediatric", "9.00 a.m - 11.00 a.m", 5500.0);
        addDoctor("pinevalley", "seth mears", "surgery", "3.00 p.m - 5.00 p.m", 8000.0);
        addDoctor("pinevalley", "emeline fulton", "cardiology", "8.00 a.m - 10.00 a.m", 4000.0);
        addDoctor("pinevalley", "jared morris", "cardiology", "9.00 a.m - 11.00 a.m", 10000.0);
        addDoctor("willowgardens", "henry foster", "paediatric", "8.00 a.m - 10.00 a.m", 10000.0);
        addDoctor("willowgardens", "ann jones", "ent", "9.00 a.m - 11.00 a.m", 7000.0);
    }

    @POST
    @Path("/{hospitalId}/categories/{category}/reserve")
    public Response reserveAppointment(@PathParam("hospitalId") String hospitalId,
                                       @PathParam("category") String category, String body) {
        if (!hospitals.containsKey(hospitalId)) {
            return error(Response.Status.NOT_FOUND, "Unknown hospital: " + hospitalId);
        }
        JSONObject request = parse(body);
        if (request == null) {
            return error(Response.Status.BAD_REQUEST, "Invalid JSON payload");
        }
        Object patient = request.get("patient");
        Object doctorName = request.get("doctor");
        if (!(patient instanceof JSONObject) || doctorName == null) {
            return error(Response.Status.BAD_REQUEST, "The payload must contain 'patient' and 'doctor'");
        }

        // Unknown doctors are added on the fly so that any doctor/hospital combination used in a lab works.
        String doctorKey = doctorName.toString().trim().toLowerCase();
        JSONObject doctor = doctors.get(hospitalId).get(doctorKey + "|" + category.toLowerCase());
        if (doctor == null) {
            doctor = addDoctor(hospitalId, doctorKey, category.toLowerCase(), DEFAULT_AVAILABILITY, DEFAULT_FEE);
        }

        JSONObject appointment = new JSONObject();
        appointment.put("appointmentNumber", appointmentCounter.incrementAndGet());
        appointment.put("doctor", doctor);
        appointment.put("patient", patient);
        appointment.put("fee", doctor.get("fee"));
        appointment.put("confirmed", false);
        if (request.get("appointment_date") != null) {
            appointment.put("appointmentDate", request.get("appointment_date"));
        }
        appointments.put((Integer) appointment.get("appointmentNumber"), appointment);
        return ok(appointment);
    }

    @GET
    @Path("/{hospitalId}/categories/appointments/{appointmentId}/fee")
    public Response getAppointmentFee(@PathParam("hospitalId") String hospitalId,
                                      @PathParam("appointmentId") String appointmentId) {
        JSONObject appointment = findAppointment(appointmentId);
        if (appointment == null) {
            return error(Response.Status.NOT_FOUND, "Invalid appointment ID: " + appointmentId);
        }
        JSONObject fee = new JSONObject();
        fee.put("patientName", ((JSONObject) appointment.get("patient")).get("name"));
        fee.put("doctorName", ((JSONObject) appointment.get("doctor")).get("name"));
        fee.put("actualFee", String.valueOf(appointment.get("fee")));
        return ok(fee);
    }

    @POST
    @Path("/healthcare/payments")
    public Response settlePayment(String body) {
        JSONObject request = parse(body);
        if (request == null) {
            return error(Response.Status.BAD_REQUEST, "Invalid JSON payload");
        }
        double actualFee;
        try {
            actualFee = Double.parseDouble(String.valueOf(request.get("fee")));
        } catch (NumberFormatException e) {
            return error(Response.Status.BAD_REQUEST, "The payload must contain a numeric 'fee'");
        }
        Object patient = request.get("patient");
        Object patientName = patient instanceof JSONObject ? ((JSONObject) patient).get("name") : patient;

        JSONObject payment = new JSONObject();
        payment.put("appointmentNo", request.get("appointmentNumber"));
        payment.put("patient", patientName);
        payment.put("actualFee", actualFee);
        payment.put("discount", PAYMENT_DISCOUNT);
        payment.put("discounted", actualFee * (100 - PAYMENT_DISCOUNT) / 100);
        payment.put("paymentID", UUID.randomUUID().toString());
        payment.put("status", "Settled");
        return ok(payment);
    }

    @POST
    @Path("/{hospitalId}/categories/admin/doctor/newdoctor")
    public Response addNewDoctor(@PathParam("hospitalId") String hospitalId, String body) {
        if (!hospitals.containsKey(hospitalId)) {
            return error(Response.Status.NOT_FOUND, "Unknown hospital: " + hospitalId);
        }
        JSONObject request = parse(body);
        if (request == null || request.get("name") == null || request.get("category") == null) {
            return error(Response.Status.BAD_REQUEST, "The payload must contain 'name' and 'category'");
        }
        String name = request.get("name").toString().trim().toLowerCase();
        String category = request.get("category").toString().trim().toLowerCase();
        JSONObject status = new JSONObject();
        if (doctors.get(hospitalId).containsKey(name + "|" + category)) {
            status.put("status", "Doctor Already Exist in the system");
            return ok(status);
        }
        double fee;
        try {
            fee = Double.parseDouble(String.valueOf(request.get("fee")));
        } catch (NumberFormatException e) {
            fee = DEFAULT_FEE;
        }
        Object availability = request.get("availability");
        addDoctor(hospitalId, name, category, availability == null ? DEFAULT_AVAILABILITY : availability.toString(),
                fee);
        status.put("status", "New Doctor Added Successfully");
        return ok(status);
    }

    private JSONObject addDoctor(String hospitalId, String name, String category, String availability, double fee) {
        JSONObject doctor = new JSONObject();
        doctor.put("name", name);
        doctor.put("hospital", hospitals.get(hospitalId));
        doctor.put("category", category);
        doctor.put("availability", availability);
        doctor.put("fee", fee);
        doctors.get(hospitalId).put(name + "|" + category, doctor);
        return doctor;
    }

    private JSONObject findAppointment(String appointmentId) {
        try {
            return appointments.get(Integer.parseInt(appointmentId.trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static JSONObject parse(String body) {
        try {
            Object parsed = new JSONParser().parse(body == null ? "" : body);
            return parsed instanceof JSONObject ? (JSONObject) parsed : null;
        } catch (ParseException e) {
            return null;
        }
    }

    private static Response ok(JSONObject payload) {
        return Response.status(Response.Status.OK).entity(payload.toJSONString())
                .type(MediaType.APPLICATION_JSON).build();
    }

    private static Response error(Response.Status status, String message) {
        JSONObject payload = new JSONObject();
        payload.put("error", message);
        return Response.status(status).entity(payload.toJSONString()).type(MediaType.APPLICATION_JSON).build();
    }
}
