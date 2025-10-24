public class Date {
    private int year;
    private int month;
    private int day;

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    // Parse from string: "YYYY-MM-DD"
    public static Date fromString(String dateStr) {
        String[] parts = dateStr.split("-");
        if (parts.length != 3) return null;
        int y = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);
        int d = Integer.parseInt(parts[2]);
        return new Date(y, m, d);
    }

    // Compare dates: returns -1, 0, 1
    public int compareTo(Date other) {
        if (this.year != other.year) return this.year - other.year;
        if (this.month != other.month) return this.month - other.month;
        return this.day - other.day;
    }

    @Override
    public String toString() {
        return year + "-" + (month < 10 ? "0" : "") + month + "-" + (day < 10 ? "0" : "") + day;
    }

    // Getters
    public int getYear() { return year; }
    public int getMonth() { return month; }
    public int getDay() { return day; }
}
