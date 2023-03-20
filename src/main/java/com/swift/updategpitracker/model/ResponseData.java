package com.swift.updategpitracker.model;

import lombok.Data;

import java.util.List;

@Data
public class ResponseData {
    private String message;
    private List<SwiftMessageMT199> messages;
    private List<String> notification;

}
