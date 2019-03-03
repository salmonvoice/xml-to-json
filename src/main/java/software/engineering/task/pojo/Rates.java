package software.engineering.task.pojo;

public class Rates {

    public static class Rates2 {
        public double USD;
        public double EUR;
        public double RUB;

        @Override
        public String toString() {
            return "{" +
                    "USD='" + USD + '\'' +
                    ", EUR='" + EUR + '\'' +
                    ", RUB='" + RUB + '\'' +
                    '}';
        }
    }

    public String date;
    public String base;
    public Rates2 rates;

    @Override
    public String toString() {
        return "Rates{" +
                "date=" + date +
                ", base='" + base + '\'' +
                ", rates=" + rates +
                '}';
    }
}
