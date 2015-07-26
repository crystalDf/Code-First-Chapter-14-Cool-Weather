package com.star.coolweather.util;


import com.star.coolweather.db.CoolWeatherDB;
import com.star.coolweather.model.City;
import com.star.coolweather.model.Country;
import com.star.coolweather.model.Province;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Utility {

    public synchronized static boolean handleProvincesResponse(
            CoolWeatherDB coolWeatherDB, String response) {

        try {

            DocumentBuilderFactory documentBuilderFactory =
                    DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = null;

            documentBuilder = documentBuilderFactory.newDocumentBuilder();

            InputStream inputStream = new ByteArrayInputStream(response.getBytes("UTF-8"));

            Document document = documentBuilder.parse(inputStream);

            Element element = document.getDocumentElement();

            return processElementForProvince(coolWeatherDB, element);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;

    }

    public synchronized static boolean handleCitiesResponse(
            CoolWeatherDB coolWeatherDB, String response, int provinceId) {

        try {

            DocumentBuilderFactory documentBuilderFactory =
                    DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = null;

            documentBuilder = documentBuilderFactory.newDocumentBuilder();

            InputStream inputStream = new ByteArrayInputStream(response.getBytes("UTF-8"));

            Document document = documentBuilder.parse(inputStream);

            Element element = document.getDocumentElement();

            return processElementForCity(coolWeatherDB, element, provinceId);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;

    }

    public synchronized static boolean handleCountriesResponse(
            CoolWeatherDB coolWeatherDB, String response, int cityId) {

        try {

            DocumentBuilderFactory documentBuilderFactory =
                    DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = null;

            documentBuilder = documentBuilderFactory.newDocumentBuilder();

            InputStream inputStream = new ByteArrayInputStream(response.getBytes("UTF-8"));

            Document document = documentBuilder.parse(inputStream);

            Element element = document.getDocumentElement();

            return processElementForCountry(coolWeatherDB, element, cityId);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;

    }

    private static boolean processElementForProvince(CoolWeatherDB coolWeatherDB,
                                                  Element element) {

        boolean isAlreadyIn = false;

        NodeList nodeList = element.getElementsByTagName("d");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element ele = (Element) nodeList.item(i);
            String d1 = ele.getAttribute("d1");
            String d4 = ele.getAttribute("d4");
            if (d1.startsWith("101")) {
                List<Province> provinceList = coolWeatherDB.loadProvinces();
                for (Province province : provinceList) {
                    if (province.getCode().equals(d1.substring(3, 5))) {
                        isAlreadyIn = true;
                        break;
                    }
                }

                if (!isAlreadyIn) {
                    Province province = new Province();

                    province.setName(d4);
                    province.setCode(d1.substring(3, 5));

                    coolWeatherDB.saveProvince(province);
                } else {
                    isAlreadyIn = false;
                }

            } else {
                break;
            }
        }

        return true;
    }

    private static boolean processElementForCity(CoolWeatherDB coolWeatherDB,
                                                 Element element, int provinceId) {

        boolean isAlreadyIn = false;

        NodeList nodeList = element.getElementsByTagName("d");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element ele = (Element) nodeList.item(i);
            String d1 = ele.getAttribute("d1");
            String d2 = ele.getAttribute("d2");

            String provinceCode = coolWeatherDB.queryProvinceCode(provinceId);

//            List<Province> provinceList = coolWeatherDB.loadProvinces();
//            for (Province province : provinceList) {
//                if (province.getId() == provinceId) {
//                    provinceCode = province.getCode();
//                    break;
//                }
//            }

            if (d1.startsWith("101")) {
                if (d1.startsWith("101" + provinceCode)) {
                    List<City> cityList = coolWeatherDB.loadCities(provinceId);
                    for (City city : cityList) {
                        if (city.getCode().equals(d1.substring(3, 7))) {
                            isAlreadyIn = true;
                            break;
                        }
                    }

                    if (!isAlreadyIn) {
                        City city = new City();

                        city.setName(d2);
                        city.setCode(d1.substring(3, 7));
                        city.setProvinceId(provinceId);

                        coolWeatherDB.saveCity(city);
                    } else {
                        isAlreadyIn = false;
                    }
                }
            } else {
                break;
            }
        }

        return true;

    }

    private static boolean processElementForCountry(CoolWeatherDB coolWeatherDB,
                                                 Element element, int cityId) {

        boolean isAlreadyIn = false;

        NodeList nodeList = element.getElementsByTagName("d");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element ele = (Element) nodeList.item(i);
            String d1 = ele.getAttribute("d1");
            String d2 = ele.getAttribute("d2");

            String cityCode = coolWeatherDB.queryCityCode(cityId);

//            List<Province> provinceList = coolWeatherDB.loadProvinces();
//            for (Province province : provinceList) {
//                List<City> cityList = coolWeatherDB.loadCities(province.getId());
//                for (City city: cityList) {
//                    if (city.getId() == cityId) {
//                        cityCode = city.getCode();
//                        break;
//                    }
//                }
//                if (!cityCode.equals("")) {
//                    break;
//                }
//            }

            if (d1.startsWith("101")) {
                if (d1.startsWith("101" + cityCode)) {
                    List<Country> countryList = coolWeatherDB.loadCountries(cityId);
                    for (Country country : countryList) {
                        if (country.getCode().equals(d1.substring(3, 9))) {
                            isAlreadyIn = true;
                            break;
                        }
                    }

                    if (!isAlreadyIn) {
                        Country country = new Country();

                        country.setName(d2);
                        country.setCode(d1.substring(3, 9));
                        country.setCityId(cityId);

                        coolWeatherDB.saveCountry(country);
                    } else {
                        isAlreadyIn = false;
                    }
                }
            } else {
                break;
            }
        }

        return true;
    }
}
