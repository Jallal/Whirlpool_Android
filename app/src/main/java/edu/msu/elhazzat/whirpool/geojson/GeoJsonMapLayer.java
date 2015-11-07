package edu.msu.elhazzat.whirpool.geojson;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.geometry.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christianwhite on 10/8/15.
 */
public class GeoJsonMapLayer {
    private GeoJson mGeoJson = null;
    private List<Polygon> mPolygons = new ArrayList<>(); // gms polygon
    private List<Polyline> mPolylines = new ArrayList<>(); // gms polyline
    private List<Point> mPoints = new ArrayList<>();
    private boolean mIsVisible = false;
    private boolean mIsDrawn = false;

    public List<Polygon> getPolygons() {
        return mPolygons;
    }

    public void setGeoJson(GeoJson json) {
        mGeoJson = json;
    }

    public GeoJson getGeoJson() {
        return mGeoJson;
    }

    public GeoJsonMapLayer(GeoJson json) {
        mGeoJson = json;
    }

    public GeoJsonMapLayer() {

    }

    /**
     * Draws the map using uniform properties.
     * @param map
     * @param fillColor
     * @param strokeColor
     * @param strokeWidth
     */
    public void draw(final GoogleMap map, final int fillColor,
                     final int strokeColor, final int strokeWidth) {
        if(!mIsDrawn && mGeoJson != null) {
            for (GeoJsonFeature feature : mGeoJson.getGeoJsonFeatures()) {
                final GeoJsonGeometry jsonGeometry = feature.getGeoJsonGeometry();
                if (jsonGeometry == null) {
                    continue;
                }
                switch(jsonGeometry.getType()) {
                    case GeoJsonConstants.POINT:
                        GeoJsonPoint point = (GeoJsonPoint) jsonGeometry.getGeometry();
                        LatLng latLng = Geometry.geoJsonCoordinateToLatLng(point.getPoints(), false);
                        Polygon polygon = map.addPolygon(new PolygonOptions()
                                .add(latLng).fillColor(fillColor).strokeWidth(strokeWidth));
                        mPolygons.add(polygon);
                        break;
                    case GeoJsonConstants.LINESTRING:
                        GeoJsonPolyline lineString = (GeoJsonPolyline) jsonGeometry.getGeometry();
                        List<LatLng> latLngList = Geometry.geoJsonCoordinateListToLatLng(lineString.getPoints(), false);
                        Polyline polyline  = map.addPolyline(new PolylineOptions()
                                .addAll(latLngList).color(strokeColor).width(strokeWidth));
                        mPolylines.add(polyline);
                        break;
                    case GeoJsonConstants.POLYGON:
                        GeoJsonPolygon poly1 = (GeoJsonPolygon) jsonGeometry.getGeometry();
                        List<LatLng> latLngPoly = Geometry.geoJsonCoordinateListToLatLng(poly1.getPoints().get(0), true);
                        int color = fillColor;
                        if(feature.getProperty("room").equals("B250") || feature.getProperty("room").equals("B205") ||
                                feature.getProperty("room").equals("B218")|| feature.getProperty("room").equals("B217")){
                         color = Color .rgb(234, 230, 245);
                        }

                        else if( feature.getProperty("room").equals("B241") ||
                                feature.getProperty("room").equals("B234")||
                                 feature.getProperty("room").equals("B219")||
                                        feature.getProperty("room").equals("B251")||feature.getProperty("room").equals("B230")){

                            color = Color .WHITE;
                        }
                        else if(feature.getProperty("room").equals("B236") ||
                            feature.getProperty("room").equals("B232")||
                                feature.getProperty("room").equals("B223") ||
                                feature.getProperty("room").equals("B247") ||
                                feature.getProperty("room").equals("B233-229") ||
                                feature.getProperty("room").equals("B235-238") ||
                                feature.getProperty("room").equals("B245-248")||
                                feature.getProperty("room").equals("B222-220")){
                            color = Color.WHITE;
                        }

                        Polygon poly2  = map.addPolygon(new PolygonOptions()
                                .addAll(latLngPoly).fillColor(color).strokeWidth(strokeWidth));
                        poly1.setGMSPolygon(poly2);
                        mPolygons.add(poly2);
                        break;
                }
            }
            mIsDrawn = true;
        }
        else {
            hide(false);
        }
    }

    /**
     * Removes layer. Will have to be redrawn to be visible again.
     */
    public void remove() {
        for(Polygon polygon : mPolygons) {
            polygon.remove();
        }
        for(Polyline polyline : mPolylines) {
            polyline.remove();
        }
    }

    /**
     * Hide the map. Does not have to be redrawn.
     * @param toShow
     */
    public void hide(boolean toShow) {
        for(Polygon polygon : mPolygons) {
            polygon.setVisible(toShow);
        }
        for(Polyline polyline : mPolylines) {
            polyline.setVisible(toShow);
        }
        mIsVisible = toShow;
    }
}