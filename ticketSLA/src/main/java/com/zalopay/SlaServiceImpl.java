package com.zalopay;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class SlaServiceImpl implements SlaService {
    private static final int MINUTES_PER_HOUR = 60;
    private static final int SECONDS_PER_MINUTE = 60;

    private static final int SECONDS_WORKING_AM = (3*MINUTES_PER_HOUR+30)*SECONDS_PER_MINUTE;
    private static final int SECONDS_WORKING_PM = (4*MINUTES_PER_HOUR+30)*SECONDS_PER_MINUTE;
    private static final int SECONDS_PER_WORKING_DAY = SECONDS_WORKING_AM + SECONDS_WORKING_PM;
    private static final int SECONDS_PER_WORKING_WEEK = (SECONDS_PER_WORKING_DAY)*5 + SECONDS_WORKING_AM;

    //Mảng lưu ánh xạ số giây làm việc tương ứng với thứ trong tuần
    private Map<DayOfWeek, Integer> secondsDayOfWeek;

    public SlaServiceImpl(){
        secondsDayOfWeek = new HashMap<>();
        initSecondsDayOfWeek();
    }

    //Khởi tạo mảng số giây làm việc tương ứng với thứ trong tuần
    private void initSecondsDayOfWeek(){
        secondsDayOfWeek.put(DayOfWeek.MONDAY,SECONDS_PER_WORKING_DAY);
        secondsDayOfWeek.put(DayOfWeek.TUESDAY,SECONDS_PER_WORKING_DAY);
        secondsDayOfWeek.put(DayOfWeek.WEDNESDAY,SECONDS_PER_WORKING_DAY);
        secondsDayOfWeek.put(DayOfWeek.THURSDAY,SECONDS_PER_WORKING_DAY);
        secondsDayOfWeek.put(DayOfWeek.FRIDAY,SECONDS_PER_WORKING_DAY);
        secondsDayOfWeek.put(DayOfWeek.SATURDAY,SECONDS_WORKING_AM);
        secondsDayOfWeek.put(DayOfWeek.SUNDAY,0);
    }

    //Tính khoảng cách giữa 2 LocalDateTime tùy vào đơn vị thời gian (unit)
    public long distance(LocalDateTime begin, LocalDateTime end, ChronoUnit unit){
        return Math.abs((LocalDateTime.from(begin)).until(end, unit));
    }

    //Khởi tạo LocalDateTime mới
    public LocalDateTime getNewLocalDateTime(LocalDateTime dateTime, int hour, int minute, int second){
        return LocalDateTime.of(dateTime.getYear(),dateTime.getMonthValue(),dateTime.getDayOfMonth(),hour,minute,second);
    }

    //Lấy số giây làm việc giữa 2 LocalDateTime
    public long getSecondBetween(LocalDateTime begin, LocalDateTime end){

        //Tạo ngày kế tiếp tính từ ngày bắt đầu
        LocalDateTime nextOfBegin = getNewLocalDateTime(begin,0,0,0);
        nextOfBegin=nextOfBegin.plusDays(1);

        //Tạo ngày trước đó tính từ ngày cuối cùng
        LocalDateTime previousOfEnd=getNewLocalDateTime(end,23,59,59);
        previousOfEnd=previousOfEnd.minusDays(1);

        if(nextOfBegin.isAfter(previousOfEnd)){
            return 0;
        }

        //Tính khoảng cách ngày
        long days = distance(nextOfBegin,previousOfEnd,ChronoUnit.DAYS) + 1;
        long weeks = days / 7; // Tính số tuần (nguyên)
        long result = weeks * SECONDS_PER_WORKING_WEEK; //Số giây làm việc có được từ số tuần vừa tính

        //Bắt đầu từ ngày kế tiếp sau số tuần nguyên,
        //dùng vòng lặp để lấy số giây làm việc theo thứ của những ngày còn lại
        LocalDateTime continuous = nextOfBegin.plusDays(weeks * 7);
        while (continuous.isBefore(previousOfEnd)){
            int seconds = secondsDayOfWeek.get(continuous.getDayOfWeek());
            result+=seconds;
            continuous=continuous.plusDays(1);
        }
        return result;
    }


    //lamda in java
    interface DateTimeOperation{
        boolean operation(LocalDateTime begin, LocalDateTime end);
    }

    public boolean operate(LocalDateTime begin, LocalDateTime end, DateTimeOperation dateTimeOperation){
        return dateTimeOperation.operation(begin, end);
    }

    //Khởi tạo 4 mốc thời gian làm việc trong ngày
    public List<LocalDateTime> initFlagBeginAndEnd(LocalDateTime dateTime){
        LocalDateTime flagBeginAM = getNewLocalDateTime(dateTime,8,30,0);
        LocalDateTime flagEndAM = getNewLocalDateTime(dateTime,12,0,0);
        LocalDateTime flagBeginPM = getNewLocalDateTime(dateTime,13,30,0);
        LocalDateTime flagEndPM = getNewLocalDateTime(dateTime,18,0,0);

        List<LocalDateTime> listFlagDateTime = new ArrayList<>();
        listFlagDateTime.add(flagBeginAM);
        listFlagDateTime.add(flagEndAM);
        listFlagDateTime.add(flagBeginPM);
        listFlagDateTime.add(flagEndPM);

        return listFlagDateTime;
    }

    //Tính ngày "rìa" còn lại
    public long calcEdge(LocalDateTime dateTime, boolean countOn){
        if(dateTime.getDayOfWeek() == DayOfWeek.SUNDAY){
            return 0;
        }

        //Đặt cờ kiểm tra ngày thứ 7
        boolean flagSaturday = false;
        if(dateTime.getDayOfWeek() == DayOfWeek.SATURDAY){
            flagSaturday=true;
        }

        //Load 4 mốc thời gian làm việc trong ngày
        List<LocalDateTime> listFlagDateTime = initFlagBeginAndEnd(dateTime);
        DateTimeOperation dateTimeOperation;
        long temp_time1, temp_time2;

        //countOn : tính tới (true), tính lùi (false)
        if(countOn == true){
            dateTimeOperation = (LocalDateTime a, LocalDateTime b)-> !a.isAfter(b); //a <= b
            temp_time1 = (flagSaturday == false) ? SECONDS_WORKING_PM : distance(listFlagDateTime.get(1), dateTime, ChronoUnit.SECONDS);
            temp_time2 = (flagSaturday == false) ? SECONDS_WORKING_PM : 0;
        }
        else{
            dateTimeOperation = (LocalDateTime a, LocalDateTime b)-> !a.isBefore(b); //a >= b
            Collections.reverse(listFlagDateTime);
            temp_time1 = SECONDS_WORKING_AM;
            temp_time2 = SECONDS_WORKING_AM;
        }

        long result = 0;

        //Tùy thuộc vào mốc thời gian và cách tính mà t sẽ tính được số giây làm việc "rìa"
        //bằng cách cộng trừ với 4 mốc thời gian
        if(operate(dateTime,listFlagDateTime.get(0),dateTimeOperation)){

            result += (flagSaturday == false) ? SECONDS_PER_WORKING_DAY : SECONDS_WORKING_AM;
        }
        else if(operate(dateTime,listFlagDateTime.get(1),dateTimeOperation)){
            result += (flagSaturday == false) ? temp_time1 + distance(listFlagDateTime.get(1), dateTime, ChronoUnit.SECONDS) : temp_time1;
        }
        else if(operate(dateTime,listFlagDateTime.get(2),dateTimeOperation)){
            result += temp_time2;
        }
        else if(operate(dateTime,listFlagDateTime.get(3),dateTimeOperation)){
            result += distance(listFlagDateTime.get(3), dateTime, ChronoUnit.SECONDS);
        }

        return result;
    }

    @Override
    public Duration calculate(LocalDateTime begin, LocalDateTime end) {

        if(begin.isAfter(end)){
            throw new IllegalArgumentException();
        }

        long result = 0;
        long distanceDays = distance(getNewLocalDateTime(begin,0,0,0),getNewLocalDateTime(end,23,59,59),ChronoUnit.DAYS);

        //begin và end không trùng ngày
        if(distanceDays>=1){
            result+=getSecondBetween(begin,end);
            result+=calcEdge(end,false) + calcEdge(begin,true);
        }
        else { // trùng ngày
            result += Math.abs(calcEdge(end,false) - calcEdge(begin,false));
        }
        return Duration.ofSeconds(result);
    }
}
