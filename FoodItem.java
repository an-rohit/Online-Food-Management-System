class FoodItem {
    private String name;
    private double price;
    private boolean available;
    private String category;
    private int popularity;

    public FoodItem(String name, double price, boolean available, String category) {
        this.name = name;
        this.price = price;
        this.available = available;
        this.category = category;
        this.popularity = 0;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return available; }
    public String getCategory() { return category; }

    public void increasePopularity() {
        popularity++;
    }

    public int getPopularity() {
        return popularity;
    }
}
