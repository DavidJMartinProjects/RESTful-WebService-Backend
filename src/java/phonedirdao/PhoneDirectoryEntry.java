package phonedirdao;

import java.util.Date;
import java.sql.Timestamp;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {"id", "phoneNo", "fname", "surname", "address", "myTimestamp", "link"})

public class PhoneDirectoryEntry {

    private int id;
    private String phoneNo;
    private String fname;
    private String surname;
    private String address;
    private Timestamp myTimestamp;
    private List<Link> link;
    Date date = new Date();
    Timestamp nowTimestamp = new Timestamp(date.getTime());

    public PhoneDirectoryEntry() {
    }

    public PhoneDirectoryEntry(int a, String b, String c, String d, String e) {
        id = a;
        phoneNo = b;
        fname = c;
        surname = d;
        address = e;
    }

    public PhoneDirectoryEntry(int a, String b, String c, String d, String e, Timestamp f) {
        id = a;
        phoneNo = b;
        fname = c;
        surname = d;
        address = e;
        myTimestamp = f;
    }

    @XmlElement
    public Timestamp getMyTimestamp() {
        return this.myTimestamp;
    }

    public void setMyTimestamp(Timestamp Timestamp) {
        this.myTimestamp = myTimestamp;
    }

    @XmlElement
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    @XmlElement
    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    @XmlElement
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @XmlElement
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @XmlElement(name = "link")
    public List<Link> getLink() {
        return link;
    }

    public void setLink(List<Link> link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "PhoneDirectoryEntry{" + "id=" + id + ",phone_No=" + phoneNo + ", firstName=" + fname + ", surname=" + surname + ", address=" + address + '}';
    }

}
