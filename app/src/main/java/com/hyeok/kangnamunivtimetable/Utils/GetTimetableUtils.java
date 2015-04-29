package com.hyeok.kangnamunivtimetable.Utils;

import android.content.Context;

import com.hyeok.kangnamunivtimetable.Activity.login;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by GwonHyeok on 14. 9. 5..
 */
public class GetTimetableUtils {

    public static void RefreshTimeTable(Context context) {
        ControlSharedPref timetablepref = new ControlSharedPref(context, "timetable.pref");
        ControlSharedPref bus_pref = new ControlSharedPref(context, "shuttlebus.pref");
        ControlSharedPref pref = new ControlSharedPref(context, null);
        /**
         * Get Timetable Part
         */
        try {
            URL siganpyo = new URL("https://m.kangnam.ac.kr/knusmart/s/s251.do");
            URLConnection urlConn = siganpyo.openConnection();
            HttpsURLConnection request = (HttpsURLConnection) urlConn;
            timetablepref.clearAll();
            String idno = pref.getValue("idno", null).replaceAll("&quot;", "\"").split(";")[0];
            String gubn = pref.getValue("gubn", null).replaceAll("&quot;", "\"").split(";")[0];
            String name = pref.getValue("name", null).replaceAll("&quot;", "\"").split(";")[0];
            String pass = pref.getValue("pass", null).replaceAll("&quot;", "\"").split(";")[0];
            String auto = pref.getValue("auto", null).replaceAll("&quot;", "\"").split(";")[0];
            String mjco = pref.getValue("mjco", null).replaceAll("&quot;", "\"").split(";")[0];
            String name_e = pref.getValue("name_e", null).replaceAll("&quot;", "\"").split(";")[0];
            String jsession = pref.getValue("jsessiion", null).replaceAll("&quot;", "\"").split(";")[0];
            request.addRequestProperty("Cookie", jsession + ";" + name + ";" + mjco + ";" + auto + ";" + pass + ";" + name_e + ";" + gubn + ";" + idno + ";");
            request.setUseCaches(false);
            request.setDoOutput(true);
            request.setDoInput(true);
            HttpURLConnection.setFollowRedirects(true);
            request.setInstanceFollowRedirects(true);
            request.setRequestMethod("GET");
            System.out.println(request.getConnectTimeout());
            InputStream inputStream = request.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            JSONParser jp = new JSONParser();
            Object oj = jp.parse(stringBuilder.toString());
            JSONObject jo = (JSONObject) oj;
            JSONArray ja = (JSONArray) jo.get("data");
            for (int rep = 0; rep < ja.size(); rep++) {
                JSONObject jo2 = (JSONObject) ja.get(rep);
                timetablepref.put("mon_" + rep, "" + jo2.get("time_day1"));
                timetablepref.put("tues" + rep, "" + jo2.get("time_day2"));
                timetablepref.put("wends" + rep, "" + jo2.get("time_day3"));
                timetablepref.put("thur" + rep, "" + jo2.get("time_day4"));
                timetablepref.put("fri" + rep, "" + jo2.get("time_day5"));
                timetablepref.put("time" + rep, "" + jo2.get("real_time").toString().replaceAll(" ", ""));
            }
            /**
             * Get Exam Time Part
             */
            ArrayList<Element> al = new ArrayList<Element>();
            Document a = Jsoup.connect("http://web.kangnam.ac.kr/edu/edu_schedule/edu_schedule.jsp").get();
            int size = a.getElementsByClass("contTable").size();
            Calendar clr = Calendar.getInstance();
            if (clr.get(Calendar.MONTH) + 1 < 7) {
                for (int i = 0; i < size; i++) {
                    al.add(a.getElementsByClass("contTable").get(i));
                    if (i == 5) break;
                }
            } else {
                for (int i = 6; i < size; i++) {
                    al.add(a.getElementsByClass("contTable").get(i));
                }
            }

            for (Element anAl : al) {
                Elements Month = anAl.getElementsByTag("tr");
                for (Element aMonth : Month) {
                    String ExamTime = aMonth.getElementsByTag("td").html();
                    if (ExamTime.contains("중간시험")) {
                        pref.put(login.MIDDLE_EXAM_PREF, aMonth.getElementsByTag("th").html());
                    } else if (ExamTime.contains("기말시험")) {
                        pref.put(login.FINAL_EXAM_PREF, aMonth.getElementsByTag("th").html());
                    }
                }
            }
            /**
             * Shuttle Bus Part
             */
            JSONParser parser = new JSONParser();
            URL bus_url = new URL("https://m.kangnam.ac.kr/knusmart/p/p128.do");
            InputStream is = bus_url.openStream();
            Scanner sc = new Scanner(is);
            String json = "";
            while (sc.hasNext()) {
                json += sc.next();
            }
            JSONObject job = (JSONObject) parser.parse(json);
            JSONArray jar = (JSONArray) job.get("data");
            for (Object aJar : jar) {
                JSONObject jar_2 = (JSONObject) aJar;
                long idx = (Long) jar_2.get("idx");
                String kh_start = (String) jar_2.get("kh_start");
                String ek_start = (String) jar_2.get("ek_start");
                bus_pref.put("kh_start_" + idx, kh_start);
                bus_pref.put("ek_start_" + idx, ek_start);
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
