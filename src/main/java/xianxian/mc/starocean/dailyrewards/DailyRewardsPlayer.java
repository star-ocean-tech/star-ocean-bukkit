package xianxian.mc.starocean.dailyrewards;

import java.util.Calendar;

public class DailyRewardsPlayer {
    private final String uuid;
    private int next = -1;
    private Calendar nextDate = Calendar.getInstance();
    private Calendar nextWeek = Calendar.getInstance();

    public DailyRewardsPlayer(String uuid) {
        this.uuid = uuid;
        this.nextDate.setTimeInMillis(0);
        this.nextWeek.setTimeInMillis(0);
    }

    public String getUuid() {
        return uuid;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public Calendar getNextDate() {
        return nextDate;
    }

    public void setNextDate(Calendar nextDate) {
        this.nextDate = nextDate;
    }

    public Calendar getNextWeek() {
        return nextWeek;
    }

    public void setNextWeek(Calendar nextWeek) {
        this.nextWeek = nextWeek;
    }

    public boolean isNextClaimable() {
        return Calendar.getInstance().after(nextDate);
    }

}
