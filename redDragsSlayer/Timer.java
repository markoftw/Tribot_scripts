package scripts.redDragsSlayer;

public class Timer
{
  private long end;
  private final long start;
  private final long period;

  public Timer(long period)
  {
    this.period = period;
    this.start = System.currentTimeMillis();
    this.end = (this.start + period);
  }

  public Timer(long period, long addition) {
    this.period = period;
    this.start = (System.currentTimeMillis() + addition);
    this.end = (this.start + period);
  }

  public long getElapsed() {
    return System.currentTimeMillis() - this.start;
  }

  public long getRemaining() {
    if (isRunning()) {
      return this.end - System.currentTimeMillis();
    }
    return 0L;
  }

  public boolean isRunning() {
    return System.currentTimeMillis() < this.end;
  }

  public void reset() {
    this.end = (System.currentTimeMillis() + this.period);
  }

  public long setEndIn(long ms) {
    this.end = (System.currentTimeMillis() + ms);
    return this.end;
  }

  public String toElapsedString() {
    return format(getElapsed());
  }

  public String toRemainingString() {
    return format(getRemaining());
  }

  public String format(long time) {
    StringBuilder t = new StringBuilder();
    long total_secs = time / 1000L;
    long total_mins = total_secs / 60L;
    long total_hrs = total_mins / 60L;
    int secs = (int)total_secs % 60;
    int mins = (int)total_mins % 60;
    int hrs = (int)total_hrs % 60;
    if (hrs < 10) {
      t.append("0");
    }
    t.append(hrs);
    t.append(":");
    if (mins < 10) {
      t.append("0");
    }
    t.append(mins);
    t.append(":");
    if (secs < 10) {
      t.append("0");
    }
    t.append(secs);
    return t.toString();
  }
}