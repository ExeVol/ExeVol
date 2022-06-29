package com.example.vaadbaitv3;

public class full_report {
    private String uri;
    private String report_n;
    private String details;

    public  full_report(){

    }
    public full_report(String uri, String report_n, String details) {
        this.uri = uri;
        this.report_n = report_n;
        this.details = details;
    }

    @Override
    public String toString() {
        return "full_report{" +
                "uri='" + uri + '\'' +
                ", report_n='" + report_n + '\'' +
                ", details='" + details + '\'' +
                '}';
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getReport_n() {
        return report_n;
    }

    public void setReport_n(String report_n) {
        this.report_n = report_n;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
