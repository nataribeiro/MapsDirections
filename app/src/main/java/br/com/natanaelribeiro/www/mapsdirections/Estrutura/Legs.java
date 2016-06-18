package br.com.natanaelribeiro.www.mapsdirections.Estrutura;

import java.util.List;

/**
 * Created by 631610277 on 18/06/16.
 */
public class Legs {
    public TextValue distance;
    public TextValue duration;
    public String end_address;
    public LatLongObj end_location;
    public String start_address;
    public LatLongObj start_location;
    public List<Step> steps;
}
