package com.db.trainstationdistancecalculatorrestapiapp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.text.DecimalFormat;

@Entity
public class StationData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer eva_NR;

    private String ds100;

    private String ifopt;

    private String name;

    private String verkehr;

    private Double laenge;

    private Double breite;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEva_NR() {
        return eva_NR;
    }

    public void setEva_NR(Integer eva_NR) {
        this.eva_NR = eva_NR;
    }

    public String getDs100() {
        return ds100;
    }

    public void setDs100(String ds100) {
        this.ds100 = ds100;
    }

    public String getIfopt() {
        return ifopt;
    }

    public void setIfopt(String ifopt) {
        this.ifopt = ifopt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVerkehr() {
        return verkehr;
    }

    public void setVerkehr(String verkehr) {
        this.verkehr = verkehr;
    }

    public Double getLaenge() {
        return laenge;
    }

    public void setLaenge(Double laenge) {
        this.laenge = laenge;
    }

    public Double getBreite() {
        return breite;
    }

    public void setBreite(Double breite) {
        this.breite = breite;
    }
}
