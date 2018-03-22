package jdbc_demo;

public class Product {
	private int transID;
	private int pid;
	private String pname;
	private int price;
	private int mid;

	public int getTransID() {
		return transID;
	}

	public void setTransID(int transID) {
		this.transID = transID;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

	public Product(int transID, int pid, String pname, int price, int mid) {
		super();
		this.transID = transID;
		this.pid = pid;
		this.pname = pname;
		this.price = price;
		this.mid = mid;
	}

}
