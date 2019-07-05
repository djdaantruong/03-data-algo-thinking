# Data structures and algorithms

- [1. Cấu trúc dữ liệu](#1-cấu-trúc-dữ-liệu)
	- [1.1. Probabilistic data structures](#11-probabilistic-data-structures)
	- [1.2. Bloom Filters](#12-bloom-filters)
	- [1.3. Cuckoo Filters](#13-cuckoo-filters)
	- [1.4. Count Min Sketch](#14-count-min-sketch)
	- [1.5. HyperLogLog](#15-hyperloglog)
	- [1.6. Trie](#16-trie)
- [2. Design Pattern](#2-design-pattern)
	- [2.1. Singleton](#21-singleton)
	- [2.2. Factory](#22-factory)
	- [2.3. Builder](#23-builder)
	- [2.4. Dependency Injection](#24-dependency-injection)
	- [2.5. State](#25-state)
- [3. Nguyên tắc lập trình](#3-nguyên-tắc-lập-trình)
	- [3.1. SOLID](#31-solid)
	- [3.2. DRY](#32-dry)
	- [3.3. KISS](#33-kiss)
	- [3.4. YAGNI](#34-yagni)
	- [3.5. Do the simplest thing that could possibly work](#35-do-the-simplest-thing-that-could-possibly-work)
	- [3.6. Clean code](#36-clean-code)
- [4. Bài tập](#4-bài-tập)
	- [4.1. Predictive text](#41-predictive-text)
	- [4.2. Hash Tables](#42-hash-tables)
	- [4.3. Tính thời gian xử lý khiếu nại](#43-tính-thời-gian-xử-lý-khiếu-nại)
- [5. Tài liệu tham khảo](#5-tài-liệu-tham-khảo)

## 1. Cấu trúc dữ liệu

### 1.1. Probabilistic data structures

- Data structure: is a `structure` that holds `data`, allowing you to extract information.

- Probabilistic:

    - Query may return a wrong answer
    - The answer is `good enough`
    - Uses a fraction of the resources i.e. memory or cpu cycles

- Có 4 loại:

    - Membership: Bloom Filter, Cuckoo Filters
    - Cardinality: Linear Counting, LogLog, SuperLogLog, HyperLogLog
    - Frequency: Count-Min Sketch, Majority Algorithm, Misra-Gries Algorithm
    - Similarity: Locality-Sensitive Hashing (LSH), MinHash, SimHash

### 1.2. Bloom Filters

**Tính chất**

- Nó cho ta biết phần tử chắc chắn không có trong tập hợp hoặc có thể nằm trong tập hợp.

- Bloom filters được gọi là `filters` (bộ lọc) vì chúng thường được sử dụng để lọc ra các segment (phân khúc) của dataset (bộ dữ liệu) mà không khớp với truy vấn.

**Cách thức hoạt động**

- Bloom filter là một bit array với m bits được gán bằng 0 tại thời điểm ban đầu.

![](https://cdncontribute.geeksforgeeks.org/wp-content/uploads/enpty_bit_array.png)

- Để chèn phần tử vào bộ lọc: Cho phần tử vào k hash functions (hàm băm), nhận được k giá trị, rồi set bits tại vị trí chỉ số mảng bằng các giá trị trả về của k hàm băm.

- Để kiểm tra xem phần tử có trong bộ lọc hay không: Cho phần tử vào k hash functions (hàm băm), nhận được k giá trị, rồi check bits trong tất cả các chỉ số của mảng:

    - Nếu tất cả các bits đã được set, thì phần tử có thể tồn tại trong bộ lọc
    - Nếu ít nhất 1 bit không được set, thì phần tử chắc chắn không tồn tại trong bộ lọc

- Thời gian cần thiết để chèn hoặc kiểm tra các phần tử là hằng số O(k), không phụ thuộc vào số lượng phần tử đã có trong bộ lọc

**Ứng dụng**

- Google BigTable, Apache HBase và Apache Cassandra sử dụng Bloom ﬁlters để giảm disk lookups cho các trường không tồn tại

- Medium sử dụng Bloom ﬁlters để tránh gợi ý các bài viết mà người dùng đã đọc trước đó

- Google Chrome web browser sử dụng Bloom ﬁlters để xác định các URLs độc hại (được chuyển sang PreﬁxSet, Issue 71832)

### 1.3. Cuckoo Filters

**Tính chất**

- Thực tế tốt hơn Bloom filter

- Hỗ trợ thêm và xóa các mục

- Hiệu suất tra cứu cao

- Cuckoo hashing - giải quyết collisions (đụng độ) bằng cách rehashing (băm lại) tới một vị trí mới

**Cách thức hoạt động**

- Các tham số:

    - 2 hash functions: h1 và h2
    - 1 mảng B với n buckets, bucket thứ i gọi là B[i]

- Input: 1 list các phần tử được chèn vào cuckoo filter (đặt là L)

- Thuật toán:

```
While L is not empty:
Let x be the first item in the list L. Remove x from the list.
If B[h1(x)] is empty:
    place x in B[h1(x)]
Else, If B[h2(x)] is empty:
    place x in B[h2(x)]
Else:
    Let y be the element in B[h2(x)].
    Prepend y to L
    place x in B[h2(x)]
```

h1(x) = hash(x)
h2(x) = h1(x) xor hash(x's fingerprint)

- Mô tả: 
    - Băm 1 entry bằng 1 hash function và chèn 1 f-bit fingerprint của entry đó vào 1 vị trí trống của 1 trong 2 buckets. 

    - Khi cả 2 buckets đầy, bộ lọc `kick` bằng các đệ quy các entry hiện có vào các bucket thay thế cho đến khi hết chỗ.

    - Tra cứu: tính lại hash function và kiểm tra cả 2 buckets cho fingerprint. Khi không tìm thấy fingerprint, thì chắn chắn phần tử không có trong bộ lọc. Ngược lại, khi tìm thấy fingerprint trong bất kỳ bucket nào, thì có thể phần tử có trong bộ lọc. 

**Ví dụ:** Insert

![](https://miro.medium.com/max/480/0*AUSm5q7_raFy7aOy.gif)

```
f = fingerprint(x);
i1 = hash(x);
i2 = i1 ⊕ hash(f);
if bucket[i1] or bucket[i2] has an empty entry then
   add f to that bucket;
   return Done;
// must relocate existing items;
i = randomly pick i1 or i2;
for n = 0; n < MaxNumKicks; n++ do
   randomly select an entry e from bucket[i];
   swap f and the fingerprint stored in entry e;
   i = i ⊕ hash(f);
   if bucket[i] has an empty entry then
      add f to bucket[i];
      return Done;
// Hashtable is considered full;
return Failure;
```

**Nếu Cuckoo filter dùng nhiều hơn 2 hash functions thì sẽ như thế nào?**

- Không có gì xảy ra và điều này không cần thiết, vì:

    - Nếu ta dùng quá nhiều hash function, sẽ mất thời gian để implement và không mang lại bất kỳ lợi ích nào
    - Ta sẽ cần nhiều không gian lưu trữ hơn khi nhiều dữ liệu chèn vào 1 bucket bằng cách thêm nhiều phần tử trên 1 bucket

### 1.4. Count Min Sketch

**Tính chất**

- Thời gian cần thiết để thêm phần tử hoặc trả về tần số của nó là hằng số O(k), giả sử rằng mọi hàm băm có thể được đánh giá trong một thời gian không đổi.

**Cách thức hoạt động**

![](http://peachyo.github.io/images/Probabilistic7.png)

- Dùng nhiều mảng tương ứng với nhiều hash functions khác nhau để tính chỉ số index

- Khi chèn vào, tại vị trí chỉ số tương ứng với giá trị các hash functions trả về, ta +1

- Khi truy vấn, tại vị trí chỉ số tương ứng với giá trị các hash functions trả về, ta trả về số nhỏ nhất trong số chúng

**Ví dụ**

```
// rows = 3, cols = 5
0 0 0 0 0
0 0 0 0 0
0 0 0 0 0
```

```
// This will insert an event of type "A".
insert("A")

// For each row, we'll hash the event type with the
// hashing function for that row to get an index k
// h0("A") - 2
// h1("A") - 4
// h2("A") - 0

// And now we index into every row with the computed k
// and increment by one.
0 0 1 0 0
0 0 0 0 1
1 0 0 0 0
```

```
insert("B")

// h0("B") - 1
// h1("B") - 4
// h2("B") - 3
0 1 1 0 0
0 0 0 0 2
1 0 0 1 0
```

```
insert("A")

// h0("A") - 2
// h1("A") - 4
// h2("A") - 0
0 1 2 0 0
0 0 0 0 3
2 0 0 1 0
```

```
getFrequency("A")

// This is our state
// 0 1 2 0 0
// 0 0 0 0 3
// 2 0 0 1 0

// We'll hash again, just like when inserting.
// h0("A") - 2
// h1("A") - 4
// h2("A") - 0

// Now for every row we'll get sketch[row, hrow("A")]
// sketch[0, 2] - 2 (the frequency on this row is 2)
// sketch[1, 4] - 3 (the frequency on this row is 3)
// sketch[2, 0] - 2 (the frequency on this row is 2)

// And now we take the min of those
// min(2, 3, 2)
2
```

**Ứng dụng**

- AT&T đã sử dụng Count-Min Sketch trong các network switches để thực hiện phân tích network trafﬁc sử dụng bộ nhớ giới hạn

### 1.5. HyperLogLog

**Tính chất**

- HyperLogLog được mô tả bởi 2 tham số:

    - p - số bit xác định 1 bucket dùng để tính trung bình (m = 2^p là số lượng buckets/substreams)

    - h - hash function

- Thuật toán HyperLogLog có thể ước tính các phần tử riêng biệt của tập > 10^9 phần tử với tỷ lệ sai là 2%

- Quan sát số lượng số 0 ở đầu tối đa cho tất cả các giá trị băm.

**Cách thức hoạt động**

![](/media/HLL0.png)

Với **const(m)** = ![](/media/const.png)

**Ví dụ**

![](/media/HLL1.png)

![](/media/HLL2.png)

**Ứng dụng**

- PFCount trong Redis

- Đếm số khách truy cập duy nhất vào một trang web

### 1.6. Trie

**Giới thiệu bài toán**

*Bài toán*: Cho một tập dữ liệu gồm N chuỗi P (P1, P2, … Pn) và chuỗi truy vấn S. Tìm tất cả các chuỗi Pi trong P có tiền tố là S?

*Ví dụ*:
Input: 
N = 5, P = {"trie", "try", "cat", "dog", "do"}
S = "tr"
Ouput:  "trie", "try"

**Thuật toán tầm thường**

Duyệt qua hết các Pi trong tập dữ liệu P, với mỗi Pi và so sánh giữa S và Pi[0...length(S)].

```
res[];
for i=1 to n:
    if (S is prefix of P[i]):
        res+=P[i]
return res
```

Độ phức tạp tìm kiếm: O(n*length(S))

**Giới thiệu Trie**

- Cấu trúc dữ liệu cây gồm các node.
- Mỗi node gồm: 
    - Danh sách các node kế tiếp.
    - Giá trị cờ hiệu: có phải là kết thúc một từ hay không.
- Đường đi từ gốc đến node kết thúc của một từ thể hiện 1 xâu, gồm các ký tự là các ký tự thuộc cạnh trên đường đi đó.

**Cấu trúc 1 Node**

![](/media/Trie-Node.png)

**Truy vấn**

![](/media/Trie-Query1.png)

![](/media/Trie-Query2.png)

**Ví dụ**

![](/media/Trie.png)

**Độ phức tạp thuật toán**

- Chi phí xây dựng: Chi phí thêm từ x Số từ trong từ điển O(maxlength(Pi) x N)

- Chi phí tìm kiếm: O(length(S))

**Ứng dụng**

- Autocomplete trong các Editor, IDE, Search Engine

## 2. Design Pattern

### 2.1. Singleton

- Singleton pattern hạn chế sự khởi tạo của một lớp và đảm bảo rằng chỉ có một phiên bản của lớp tồn tại.

- Singleton class phải cung cấp một global access point để lấy thể hiện (instance) của lớp.

- Singleton pattern được sử dụng cho logging, drivers objects, caching và thread pool.

- Singleton design pattern cũng được sử dụng trong các design patterns khác như Abstract Factory, Builder, Prototype, Facade,...

- Singleton design pattern cũng được sử dụng trong core java classes, ví dụ java.lang.Runtime, java.awt.Desktop.

- Để implement một Singleton pattern, chúng ta có các cách tiếp cận khác nhau nhưng tất cả chúng đều có các khái niệm phổ biến sau.

    - Private constructor để hạn chế sự khởi tạo của lớp từ các lớp khác.
    - Private static variable của cùng một class là thể hiện duy nhất của lớp.
    - Public static method trả về thể hiện của lớp, đây là global access point cho bên ngoài để lấy thể hiện của singleton class.

**Eager initialization**

Trong eager initialization, thể hiện của lớp Singleton được tạo tại thời điểm class loading, đây là phương pháp dễ nhất để tạo một lớp singleton nhưng nó có một nhược điểm là thể hiện đó được tạo ra mặc dù client application có thể không sử dụng nó.

```
public class EagerInitializedSingleton {
    
    private static final EagerInitializedSingleton instance = new EagerInitializedSingleton();
    
    //private constructor to avoid client applications to use constructor
    private EagerInitializedSingleton(){}

    public static EagerInitializedSingleton getInstance(){
        return instance;
    }
}
```

Nếu lớp singleton của bạn không sử dụng nhiều tài nguyên, đây là cách tiếp cận hữu dụng. Nhưng trong hầu hết các trường hợp, các lớp Singleton được tạo cho các tài nguyên như  File System, Database connections, v.v. Chúng ta nên tránh việc khởi tạo cho đến khi client gọi phương thức getInstance. Ngoài ra, phương pháp này không cung cấp bất kỳ tùy chọn nào cho việc xử lý ngoại lệ.

**Static block initialization**

Nó cũng giống như eager initialization, ngoại trừ việc thể hiện của lớp được tạo trong static block cung cấp tùy chọn để xử lý ngoại lệ.

```
public class StaticBlockSingleton {

    private static StaticBlockSingleton instance;
    
    private StaticBlockSingleton(){}
    
    //static block initialization for exception handling
    static{
        try{
            instance = new StaticBlockSingleton();
        }catch(Exception e){
            throw new RuntimeException("Exception occured in creating singleton instance");
        }
    }
    
    public static StaticBlockSingleton getInstance(){
        return instance;
    }
}
```

Cả eager initialization và static block initialization đều tạo ra thể hiện ngay cả trước khi nó được sử dụng và đó không phải là cách thực hiện tốt nhất để sử dụng. Vì vậy, trong các phần tiếp theo, chúng ta sẽ tìm hiểu cách tạo một lớp Singleton hỗ trợ lazy initialization.

**Lazy Initialization**

Lazy Initialization tạo ra thể hiện trong global access method.

```
public class LazyInitializedSingleton {

    private static LazyInitializedSingleton instance;
    
    private LazyInitializedSingleton(){}
    
    public static LazyInitializedSingleton getInstance(){
        if(instance == null){
            instance = new LazyInitializedSingleton();
        }
        return instance;
    }
}
```

Việc triển khai trên hoạt động tốt trong single-threaded environment  nhưng khi nói đến các multithreaded systems, nó có thể gây ra sự cố nếu nhiều threads nằm trong điều kiện if cùng một lúc. Nó sẽ phá hủy mẫu singleton và cả hai threads sẽ nhận được các thể hiện khác nhau của lớp singleton. Trong phần tiếp theo, chúng ta sẽ thấy các cách khác nhau để tạo một thread-safe singleton class.

**Thread Safe Singleton**

Cách dễ dàng để tạo một thread-safe singleton class là làm cho global access method được đồng bộ hóa (synchronized), để chỉ một luồng có thể thực thi phương thức này tại một thời điểm.

```
public class ThreadSafeSingleton {

    private static ThreadSafeSingleton instance;
    
    private ThreadSafeSingleton(){}
    
    public static synchronized ThreadSafeSingleton getInstance(){
        if(instance == null){
            instance = new ThreadSafeSingleton();
        }
        return instance;
    }
}
```

Việc triển khai trên hoạt động tốt và cung cấp thread-safety nhưng nó làm giảm hiệu năng do chi phí liên quan đến synchronized method, mặc dù chúng ta chỉ cần nó cho một số luồng đầu tiên có thể tạo thể hiện riêng biệt. Để tránh chi phí quá cao này, nguyên tắc **double checked locking** được sử dụng. Trong cách tiếp cận này, synchronized block  được sử dụng bên trong điều kiện if với một kiểm tra bổ sung để đảm bảo rằng chỉ có một phiên bản của lớp singleton được tạo.

```
public static ThreadSafeSingleton getInstanceUsingDoubleLocking(){
    if(instance == null){
        synchronized (ThreadSafeSingleton.class) {
            if(instance == null){
                instance = new ThreadSafeSingleton();
            }
        }
    }
    return instance;
}
```

**Bill Pugh Singleton Implementation**

Bill Pugh đã đưa ra một cách tiếp cận để tạo ra lớp Singleton bằng cách sử dụng một inner static helper class.

```
public class BillPughSingleton {

    private BillPughSingleton(){}
    
    private static class SingletonHelper{
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }
    
    public static BillPughSingleton getInstance(){
        return SingletonHelper.INSTANCE;
    }
}
```

Lưu ý private inner static class có chứa thể hiện của lớp singleton. Khi lớp singleton được load, lớp SingletonHelper không được load vào bộ nhớ và chỉ khi ai đó gọi phương thức getInstance, lớp này mới được load và tạo thể hiện lớp Singleton.

Đây là cách tiếp cận được sử dụng rộng rãi nhất cho lớp Singleton vì nó không yêu cầu đồng bộ hóa.

**Using Reflection to destroy Singleton Pattern**

Reflection có thể được sử dụng để phá hủy tất cả các phương pháp singleton implementation ở trên.

```
import java.lang.reflect.Constructor;

public class ReflectionSingletonTest {

    public static void main(String[] args) {
        EagerInitializedSingleton instanceOne = EagerInitializedSingleton.getInstance();
        EagerInitializedSingleton instanceTwo = null;
        try {
            Constructor[] constructors = EagerInitializedSingleton.class.getDeclaredConstructors();
            for (Constructor constructor : constructors) {
                //Below code will destroy the singleton pattern
                constructor.setAccessible(true);
                instanceTwo = (EagerInitializedSingleton) constructor.newInstance();
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(instanceOne.hashCode());
        System.out.println(instanceTwo.hashCode());
    }
}
```

**Enum Singleton**

Để khắc phục tình trạng này với Reflection, Joshua Bloch đề nghị sử dụng Enum để triển khai mẫu thiết kế Singleton vì Java đảm bảo rằng mọi giá trị enum chỉ được khởi tạo một lần trong chương trình Java. Do các giá trị Java Enum có thể truy cập được trên toàn cục, nên singleton cũng vậy. Hạn chế là enum là có phần không linh hoạt; ví dụ, nó không cho phép lazy initialization.

```
public enum EnumSingleton {

    INSTANCE;
    
    public static void doSomething(){
        //do something
    }
}
```

**Serialization and Singleton**

Đôi khi trong các hệ thống phân tán, chúng ta cần implement Serializable interface trong lớp Singleton để có thể lưu trữ trạng thái của nó trong file system và truy xuất nó vào một thời điểm sau.

```
import java.io.Serializable;

public class SerializedSingleton implements Serializable{

    private static final long serialVersionUID = -7604766932017737115L;

    private SerializedSingleton(){}
    
    private static class SingletonHelper{
        private static final SerializedSingleton instance = new SerializedSingleton();
    }
    
    public static SerializedSingleton getInstance(){
        return SingletonHelper.instance;
    }
}
```

Vấn đề với serialized singleton class là bất cứ khi nào chúng ta deserialize nó, nó sẽ tạo ra một thể hiện mới của lớp.

```
public class SingletonSerializedTest {

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
        SerializedSingleton instanceOne = SerializedSingleton.getInstance();
        ObjectOutput out = new ObjectOutputStream(new FileOutputStream(
                "filename.ser"));
        out.writeObject(instanceOne);
        out.close();
        
        //deserailize from file to object
        ObjectInput in = new ObjectInputStream(new FileInputStream(
                "filename.ser"));
        SerializedSingleton instanceTwo = (SerializedSingleton) in.readObject();
        in.close();
        
        System.out.println("instanceOne hashCode="+instanceOne.hashCode());
        System.out.println("instanceTwo hashCode="+instanceTwo.hashCode());
    }
}
```

Output là:

```
instanceOne hashCode=2011117821
instanceTwo hashCode=109647522
```

Vì vậy, nó phá hủy mẫu singleton, để khắc phục tình huống này, tất cả những gì chúng ta cần làm là implement phương thức readResolve() trong lớp Singleton.

```
protected Object readResolve() {
    return getInstance();
}
```

### 2.2. Factory

- Factory design pattern được sử dụng khi chúng ta có 1 super class với nhiều sub-classes và dựa vào input, chúng ta cần trả về 1 trong các sub-class. 
- Mẫu này nhận trách nhiệm khởi tạo 1 class từ client program đến factory class.

**Factory Design Pattern Super Class**

Super class trong factory design pattern có thể là interface, abstract class hoặc một class bình thường.

```
public abstract class Computer {
	
	public abstract String getRAM();
	public abstract String getHDD();
	public abstract String getCPU();
	
	@Override
	public String toString(){
		return "RAM= "+this.getRAM()+", HDD="+this.getHDD()+", CPU="+this.getCPU();
	}
}
```

**Factory Design Pattern Sub Classes**

```
public class PC extends Computer {

	private String ram;
	private String hdd;
	private String cpu;
	
	public PC(String ram, String hdd, String cpu){
		this.ram=ram;
		this.hdd=hdd;
		this.cpu=cpu;
	}
	@Override
	public String getRAM() {
		return this.ram;
	}

	@Override
	public String getHDD() {
		return this.hdd;
	}

	@Override
	public String getCPU() {
		return this.cpu;
	}

}
```

```
public class Server extends Computer {

	private String ram;
	private String hdd;
	private String cpu;
	
	public Server(String ram, String hdd, String cpu){
		this.ram=ram;
		this.hdd=hdd;
		this.cpu=cpu;
	}
	@Override
	public String getRAM() {
		return this.ram;
	}

	@Override
	public String getHDD() {
		return this.hdd;
	}

	@Override
	public String getCPU() {
		return this.cpu;
	}

}
```

**Factory Class**

```
public class ComputerFactory {

	public static Computer getComputer(String type, String ram, String hdd, String cpu){
		if("PC".equalsIgnoreCase(type)) return new PC(ram, hdd, cpu);
		else if("Server".equalsIgnoreCase(type)) return new Server(ram, hdd, cpu);
		
		return null;
	}
}
```

- Factory class có thể dùng Singleton hoặc có thể để cho phương thức trả về sub-class dạng static.

- Dựa trên tham số đầu vào, các sub-class được tạo và trả về qua hàm `getComputer`.

![](https://cdn.journaldev.com/wp-content/uploads/2013/05/factory-pattern-java.png)

**Main**

```
public class TestFactory {

	public static void main(String[] args) {
		Computer pc = ComputerFactory.getComputer("pc","2 GB","500 GB","2.4 GHz");
		Computer server = ComputerFactory.getComputer("server","16 GB","1 TB","2.9 GHz");
		System.out.println("Factory PC Config::"+pc);
		System.out.println("Factory Server Config::"+server);
	}

}
```

**Output**

```
Factory PC Config::RAM= 2 GB, HDD=500 GB, CPU=2.4 GHz
Factory Server Config::RAM= 16 GB, HDD=1 TB, CPU=2.9 GHz
```

**Factory Design Pattern Examples in JDK**

- java.util.Calendar, ResourceBundle and NumberFormat getInstance() methods dùng Factory pattern.

- valueOf() method trong wrapper classes như Boolean, Integer,...

### 2.3. Builder

- Các vấn đề chính với các mẫu thiết kế Factory và Abstract Factory khi Object chứa rất nhiều thuộc tính.
    - Qúa nhiều đối số để chuyển từ client program đến Factory class
    - Một số tham số có thể là tùy chọn nhưng trong mẫu Factory, chúng ta buộc phải gửi tất cả các tham số và tham số tùy chọn cần gửi dưới dạng NULL.

- Builder pattern giải quyết vấn đề số lượng lớn các tham số tùy chọn và trạng thái không nhất quán bằng cách cung cấp cách xây dựng đối tượng từng bước và cung cấp một phương thức sẽ thực sự trả về đối tượng cuối cùng.

**Builder Design Pattern in Java**

1. Trước tiên, ta cần tạo 1 static nested class và sau đó copy tất cả các đối số từ outer class tới Builder class. Chúng ta nên tuân theo quy ước đặt tên và nếu tên lớp là Computer thì lớp builder nên được đặt tên là ComputerBuilder.

2. Java Builder class nên có public constructor với tất cả các thuộc tính bắt buộc làm tham số.

3. Java Builder class nên có các phương thức để set các tham số tùy chọn và nó sẽ trả về cùng một đối tượng Builder sau khi set thuộc tính tùy chọn.

4. Cuối cùng, cung cấp một phương thức build() trong builder class sẽ trả về Object cần thiết cho client program. Đối với điều này, chúng ta cần phải có một private constructor trong Class với Builder class làm đối số.

```
public class Computer {
	
	//required parameters
	private String HDD;
	private String RAM;
	
	//optional parameters
	private boolean isGraphicsCardEnabled;
	private boolean isBluetoothEnabled;
	

	public String getHDD() {
		return HDD;
	}

	public String getRAM() {
		return RAM;
	}

	public boolean isGraphicsCardEnabled() {
		return isGraphicsCardEnabled;
	}

	public boolean isBluetoothEnabled() {
		return isBluetoothEnabled;
	}
	
	private Computer(ComputerBuilder builder) {
		this.HDD=builder.HDD;
		this.RAM=builder.RAM;
		this.isGraphicsCardEnabled=builder.isGraphicsCardEnabled;
		this.isBluetoothEnabled=builder.isBluetoothEnabled;
	}
	
	//Builder Class
	public static class ComputerBuilder{

		// required parameters
		private String HDD;
		private String RAM;

		// optional parameters
		private boolean isGraphicsCardEnabled;
		private boolean isBluetoothEnabled;
		
		public ComputerBuilder(String hdd, String ram){
			this.HDD=hdd;
			this.RAM=ram;
		}

		public ComputerBuilder setGraphicsCardEnabled(boolean isGraphicsCardEnabled) {
			this.isGraphicsCardEnabled = isGraphicsCardEnabled;
			return this;
		}

		public ComputerBuilder setBluetoothEnabled(boolean isBluetoothEnabled) {
			this.isBluetoothEnabled = isBluetoothEnabled;
			return this;
		}
		
		public Computer build(){
			return new Computer(this);
		}
	}
}
```

**Main**

```
public class TestBuilderPattern {

	public static void main(String[] args) {
		//Using builder to get the object in a single line of code and 
                //without any inconsistent state or arguments management issues		
		Computer comp = new Computer.ComputerBuilder(
				"500 GB", "2 GB").setBluetoothEnabled(true)
				.setGraphicsCardEnabled(true).build();
	}
}
```

**Builder Design Pattern Example in JDK**

- java.lang.StringBuilder#append() (unsynchronized)

- java.lang.StringBuffer#append() (synchronized)

### 2.4. Dependency Injection

Dependency Injection design pattern cho phép chúng tôi loại bỏ các hard-coded dependencies và làm cho ứng dụng được có thể mở rộng và maintain được. Chúng ta có thể  implement dependency injection in java để chuyển dependency resolution từ compile-time sang runtime.

**Java Dependency Injection**

- Ứng dụng có class `EmailService` để gửi mail.

```
public class EmailService {

	public void sendEmail(String message, String receiver){
		//logic to send email
		System.out.println("Email sent to "+receiver+ " with Message="+message);
	}
}
```

```
public class MyApplication {

	private EmailService email = new EmailService();
	
	public void processMessages(String msg, String rec){
		//do some msg validation, manipulation logic etc
		this.email.sendEmail(msg, rec);
	}
}
```

```
public class MyLegacyTest {

	public static void main(String[] args) {
		MyApplication app = new MyApplication();
		app.processMessages("Hi Pankaj", "pankaj@abc.com");
	}

}
```

- Thoạt nhìn, dường như không có gì sai với việc thực hiện ở trên. Nhưng code logic trên có những hạn chế nhất định.
    - Lớp MyApplication có trách nhiệm khởi tạo email service và sau đó sử dụng nó. Điều này dẫn đến hard-coded dependency. Nếu chúng ta muốn chuyển sang một số email service nâng cao khác trong tương lai, nó sẽ yêu cầu thay đổi code trong lớp MyApplication. Điều này làm cho ứng dụng của chúng ta khó mở rộng và nếu email service được sử dụng trong nhiều lớp thì điều đó sẽ còn khó hơn nữa.
    - Nếu chúng ta muốn mở rộng ứng dụng của mình để cung cấp thêm tính năng nhắn tin, chẳng hạn như tin nhắn SMS hoặc Facebook thì chúng ta sẽ cần phải viết một ứng dụng khác cho điều đó. Điều này sẽ liên quan đến thay đổi code trong các lớp application và trong các lớp client.
    - Việc kiểm tra ứng dụng sẽ rất khó khăn vì ứng dụng của chúng ta đang trực tiếp tạo ra thể hiện email service.

- Chúng ta có thể loại bỏ việc tạo thể hiện email service khỏi lớp MyApplication bằng cách nhờ một constructor yêu cầu email service làm đối số.

```
public class MyApplication {

	private EmailService email = null;
	
	public MyApplication(EmailService svc){
		this.email=svc;
	}
	
	public void processMessages(String msg, String rec){
		//do some msg validation, manipulation logic etc
		this.email.sendEmail(msg, rec);
	}
}
```

- Nhưng trong trường hợp này, chúng ta đang yêu cầu các client applications hoặc các test classes khởi tạo email service, đây không phải là một quyết định thiết kế tốt.

- Bây giờ, chúng ta xem làm thế nào chúng ta có thể áp dụng java dependency injection pattern để giải quyết tất cả các vấn đề với việc thực hiện ở trên. Dependency Injection trong java yêu cầu các điều sau:
    - Service components nên được thiết kế với lớp cơ sở hoặc interface.
    - Consumer classes nên được viết theo service interface.
    - Injector classes sẽ khởi tạo các services và sau đó là các consumer classes.

**Java Dependency Injection – Service Components**

- Tạo interface MessageService để EmailService implement

```
public interface MessageService {
	void sendMessage(String msg, String rec);
}
```

```
public class EmailServiceImpl implements MessageService {

	@Override
	public void sendMessage(String msg, String rec) {
		//logic to send email
		System.out.println("Email sent to "+rec+ " with Message="+msg);
	}
}
```

- Có thêm SMSService

```
public class SMSServiceImpl implements MessageService {

	@Override
	public void sendMessage(String msg, String rec) {
		//logic to send SMS
		System.out.println("SMS sent to "+rec+ " with Message="+msg);
	}
}
```

**Java Dependency Injection – Service Consumer**

- Chúng ta không yêu cầu phải có interface cơ sở cho consumer classes nhưng ta sẽ có 1 Consumer interface để consumer classes implement.

```
public interface Consumer {
	void processMessages(String msg, String rec);
}
```

```
public class MyDIApplication implements Consumer{

	private MessageService service;
	
	public MyDIApplication(MessageService svc){
		this.service=svc;
	}
	
	@Override
	public void processMessages(String msg, String rec){
		//do some msg validation, manipulation logic etc
		this.service.sendMessage(msg, rec);
	}
}
```

**Java Dependency Injection – Injectors Classes**

```
public interface MessageServiceInjector {
	public Consumer getConsumer();
}
```

```
public class EmailServiceInjector implements MessageServiceInjector {

	@Override
	public Consumer getConsumer() {
		return new MyDIApplication(new EmailServiceImpl());
	}
}
```

```
public class SMSServiceInjector implements MessageServiceInjector {

	@Override
	public Consumer getConsumer() {
		return new MyDIApplication(new SMSServiceImpl());
	}
}
```

- Test hàm main

```
public class MyMessageDITest {

	public static void main(String[] args) {
		String msg = "Hi Pankaj";
		String email = "pankaj@abc.com";
		String phone = "4088888888";
		MessageServiceInjector injector = null;
		Consumer app = null;
		
		//Send email
		injector = new EmailServiceInjector();
		app = injector.getConsumer();
		app.processMessages(msg, email);
		
		//Send SMS
		injector = new SMSServiceInjector();
		app = injector.getConsumer();
		app.processMessages(msg, phone);
	}
}
```

- Lớp application chỉ chịu trách nhiệm sử dụng service. Các lớp service được tạo ra trong injectors. Ngoài ra nếu chúng ta phải mở rộng thêm ứng dụng của mình để cho phép nhắn tin facebook, chúng ta sẽ chỉ phải viết các lớp service và các lớp injector.

**Ưu điểm**

- Tách biệt các liên quan
- Cắt giảm code trong application class bởi vì tất cả công việc để khởi tạo dependencies được xử lý bởi injector component
- Configurable components làm ứng dụng dễ mở rộng
- Unit testing dễ với mock objects

**Nhược điểm**

- Nếu sử dụng quá mức, có thể dẫn đến vấn đề về maintain bởi vì ảnh hưởng của các thay đổi trong runtime
- Dependency injection trong java ẩn các service class dependencies có thể dẫn đến các runtime errors mà đáng lẽ sẽ bị bắt lúc compile time.

### 2.5. State

- Nếu chúng ta phải thay đổi hành vi của một đối tượng dựa trên trạng thái của nó, chúng ta có thể có một biến trạng thái trong Đối tượng. Sau đó sử dụng if-else để thực hiện các hành động khác nhau dựa trên trạng thái. State design pattern sẽ thực hiện việc trên thông qua Context và State implementations.

- Giả sử, ta muốn implement TV Remote với một nút đơn giản để thực hiện hành động. Nếu state là ON, nó sẽ bật TV và nếu state là OFF, nó sẽ tắt TV.

**Tiếp cận thông thường**

```
public class TVRemoteBasic {

	private String state="";
	
	public void setState(String state){
		this.state=state;
	}
	
	public void doAction(){
		if(state.equalsIgnoreCase("ON")){
			System.out.println("TV is turned ON");
		}else if(state.equalsIgnoreCase("OFF")){
			System.out.println("TV is turned OFF");
		}
	}

	public static void main(String args[]){
		TVRemoteBasic remote = new TVRemoteBasic();
		
		remote.setState("ON");
		remote.doAction();
		
		remote.setState("OFF");
		remote.doAction();
	}
}
```

**State Design Pattern Interface**

```
public interface State {
	public void doAction();
}
```

**State Design Pattern Concrete State Implementations**

```
public class TVStartState implements State {

	@Override
	public void doAction() {
		System.out.println("TV is turned ON");
	}
}
```

```
public class TVStopState implements State {

	@Override
	public void doAction() {
		System.out.println("TV is turned OFF");
	}
}
```

**State Design Pattern Context Implementation**

```
public class TVContext implements State {

	private State tvState;

	public void setState(State state) {
		this.tvState=state;
	}

	public State getState() {
		return this.tvState;
	}

	@Override
	public void doAction() {
		this.tvState.doAction();
	}
}
```

**State Design Pattern Test Program**

```
public class TVRemote {

	public static void main(String[] args) {
		TVContext context = new TVContext();
		State tvStartState = new TVStartState();
		State tvStopState = new TVStopState();
		
		context.setState(tvStartState);
		context.doAction();
		
		
		context.setState(tvStopState);
		context.doAction();
	}
}
```

## 3. Nguyên tắc lập trình

### 3.1. SOLID

**1. Single responsibility principle**

>Một class chỉ nên giữ 1 trách nhiệm duy nhất 
>(Chỉ có thể sửa đổi class với 1 lý do duy nhất)

VD: 1 class vi phạm nguyên lý:

```
public class ReportManager()
{
   public void ReadDataFromDB();
   public void ProcessData();
   public void PrintReport();
}
```

Class này giữ tới 3 trách nhiệm: Đọc dữ liệu từ DB, xử lý dữ liệu, in kết quả. Do đó, chỉ cần ta thay đổi DB, thay đổi cách xuất kết quả,... ta sẽ phải sửa đổi class này. Càng về sau class sẽ càng phình to ra. Theo đúng nguyên lý, ta phải tách class này ra làm 3 class riêng. Tuy số lượng class nhiều hơn những việc sửa chữa sẽ đơn giản hơn, class ngắn hơn nên cũng ít bug hơn.

**2. Open/closed principle**

>Có thể thoải mái mở rộng 1 class, nhưng không được sửa đổi bên trong class đó 
>(open for extension but closed for modification).

Theo nguyên lý này, mỗi khi ta muốn thêm chức năng,... cho chương trình, chúng ta nên viết class mới mở rộng class cũ (bằng cách kế thừa hoặc sở hữu class cũ) không nên sửa đổi class cũ.

**3. Liskov Substitution Principle**

>Trong một chương trình, các object của class con có thể thay thế class cha mà không làm thay đổi tính đúng đắn của chương trình

Hãy tưởng tượng bạn có 1 class cha tên Vịt. Các class VịtBầu, VịtXiêm có thể kế thừa class này, chương trình chạy bình thường. Tuy nhiên nếu ta viết class VịtChạyPin, cần pin mới chạy được. Khi class này kế thừa class Vịt, vì không có pin không chạy được, sẽ gây lỗi. Đó là 1 trường hợp vi phạm nguyên lý này.

**4. Interface Segregation Principle**

>Thay vì dùng 1 interface lớn, ta nên tách thành nhiều interface nhỏ, với nhiều mục đích cụ thể

Hãy tưởng tượng chúng ta có 1 interface lớn, khoảng 100 methods. Việc implements sẽ khá cực khổ, ngoài ra còn có thể dư thừa vì 1 class không cần dùng hết 100 method. Khi tách interface ra thành nhiều interface nhỏ, gồm các method liên quan tới nhau, việc implement và quản lý sẽ dễ hơn.

**5. Dependency inversion principle**

>1. Các module cấp cao không nên phụ thuộc vào các modules cấp thấp. Cả 2 nên phụ thuộc vào abstraction.

>2. Interface (abstraction) không nên phụ thuộc vào chi tiết, mà ngược lại. (Các class giao tiếp với nhau thông qua interface, không phải thông qua implementation)

VD:

- Chúng ta đều biết 2 loại đèn: đèn tròn và đèn huỳnh quang. Chúng cùng có đuôi tròn, do đó ta có thể thay thế đèn tròn bằng đèn huỳnh quanh cho nhau 1 cách dễ dàng.

- Ở đây, interface chính là đuôi tròn, implementation là bóng đèn tròn và bóng đèn huỳnh quang. Ta có thể swap dễ dàng giữa 2 loại bóng vì ổ điện chỉ quan tâm tới interface (đuôi tròn), không quan tâm tới implementation.

- Trong code cũng vậy, khi áp dụng Dependency Inverse, ta chỉ cần quan tâm tới interface. Để kết nối tới database, ta chỉ cần gọi hàm Get, Save,... của Interface IDataAccess. Khi thay database, ta chỉ cần thay implementation của interface này.

### 3.2. DRY

DRY là viết tắt của `Don't Repeat Yourself`. Nó hướng đến sự mục tiêu giảm sự lặp lại của thông tin.

**Các vi phạm của DRY**

"We enjoy typing" (hoặc, "Wasting everyone's time.") nghĩa là viết cùng một code hoặc logic nhiều lần. Sẽ rất khó để quản lý code và nếu logic thay đổi, thì chúng ta phải thay đổi ở tất cả những nơi chúng ta đã viết code, do đó làm lãng phí thời gian của mọi người.

**Thực hiện DRY như thế nào?**

- Để tránh vi phạm nguyên tắc DRY, hãy chia hệ thống của bạn thành nhiều phần. 
- Chia code và logic của bạn thành các đơn vị tái sử dụng nhỏ hơn và sử dụng code đó bằng cách gọi nó ở nơi bạn muốn. 
- Đừng viết các phương thức dài, nhưng phân chia logic và cố gắng sử dụng phần hiện có trong phương thức của bạn.

**Lợi ích của DRY**

- Ít code là tốt: Nó tiết kiệm thời gian và công sức, dễ bảo trì và cũng làm giảm nguy cơ lỗi.

- Một ví dụ điển hình của nguyên tắc DRY là helper class trong các enterprise libraries, trong đó mỗi đoạn code là duy nhất trong các libraries và helper classes.

**Ví dụ**

- Code lặp:

```
//flow A
let username = getUserName();
let email = getEmail();
let user = { username, email };
client.post(user).then(/*do stuff*/);

//flow B
let username = getUserName();
let email = getEmail();
let user = { username, email };
client.get(user).then(/*do stuff*/);
```

- DRY:

```
function getUser(){
	return {
		user:getUserName();
		password:getPassword();
	}
}
//flow A
client.post(getUser()).then(/*do stuff*/ );
//flow B
client.get(getUser()).then(/*do stuff*/);
```

### 3.3. KISS

- KISS là viết tắt của `Keep It Simple, Stupid`. Nguyên tắc KISS là mô tả để giữ cho code đơn giản và rõ ràng, làm cho nó dễ hiểu. Xét cho cùng, ngôn ngữ lập trình là để con người hiểu - máy tính chỉ có thể hiểu 0 và 1 - vì vậy hãy tiếp tục code đơn giản và dễ hiểu. Giữ cho phương thức nhỏ. Mỗi phương thức không bao giờ được quá 40-50 dòng.

- Mỗi phương thức chỉ nên giải quyết một vấn đề nhỏ, không có nhiều trường hợp sử dụng. Nếu bạn có nhiều điều kiện trong phương thức, hãy chia chúng thành các phương thức nhỏ hơn. Nó sẽ không chỉ dễ đọc và bảo trì hơn mà còn có thể giúp tìm ra lỗi nhanh hơn rất nhiều.

**Các vi phạm của KISS**

Tất cả chúng ta đều đã trải qua tình huống mà chúng ta có công việc phải làm trong một dự án và tìm thấy một số code lộn xộn được viết. Điều đó làm chúng ta tự hỏi tại sao họ viết những dòng không cần thiết này. Chỉ cần nhìn vào hai đoạn code dưới đây. Cả hai phương thức đều làm cùng một điều. Bây giờ bạn phải quyết định sử dụng cái nào:

```
public String weekday1(int day) {
    switch (day) {
        case 1:
            return "Monday";
        case 2:
            return "Tuesday";
        case 3:
            return "Wednesday";
        case 4:
            return "Thursday";
        case 5:
            return "Friday";
        case 6:
            return "Saturday";
        case 7:
            return "Sunday";
        default:
            throw new InvalidOperationException("day must be in range 1 to 7");
    }
}
public String weekday2(int day) {
    if ((day < 1) || (day > 7)) throw new InvalidOperationException("day must be in range 1 to 7");
    string[] days = {
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday"
    };
    return days[day - 1];
}
```

**Thực hiện KISS như thế nào?**

Để tránh vi phạm nguyên tắc KISS, hãy thử viết code đơn giản. Hãy nghĩ về nhiều giải pháp cho vấn đề của bạn, sau đó chọn giải pháp tốt nhất, đơn giản nhất và chuyển đổi nó thành code của bạn. Bất cứ khi nào bạn tìm thấy code dài, hãy chia code đó thành nhiều phương thức - nhấp chuột phải và refactor trong editor. Cố gắng viết các khối code nhỏ thực hiện một nhiệm vụ.

**Lợi ích của KISS**

Nếu chúng ta có một số chức năng được viết bởi một developer và nó được viết code lộn xộn và nếu chúng ta yêu cầu một developer khác thực hiện sửa đổi trong code đó, thì trước tiên, họ phải hiểu code. Rõ ràng, nếu code được viết đơn giản, thì sẽ không có bất kỳ khó khăn nào trong việc hiểu code đó, và cũng sẽ dễ dàng sửa đổi.

### 3.4. YAGNI

YAGNI là viết tắt của `You aren't gonna need it`. Nó nói rằng lập trình viên không nên thêm chức năng cho đến khi thấy cần thiết.

### 3.5. Do the simplest thing that could possibly work

- Quy tắc quan trọng nhất trong lập trình là luôn luôn làm điều đơn giản nhất có thể làm. Không phải là điều ngu ngốc nhất, không phải là thứ mà rõ ràng không thể làm. Nhưng sự đơn giản là yếu tố đóng góp quan trọng nhất cho khả năng tiến bộ nhanh chóng.

- Đầu tiên, triển khai một khả năng mới theo cách đơn giản nhất. Bạn có thể nghĩ điều đó "có thể hoạt động". Đừng xây dựng nhiều kiến ​​trúc thượng tầng tuyệt vời, đừng làm bất cứ điều gì lạ mắt, chỉ cần đặt nó vào. Thậm chí, sử dụng một câu lệnh if. Làm cho code vượt qua UnitTests cho tính năng mới (và tất cả các tính năng, như mọi khi).

- Thứ hai, và điều này rất quan trọng đối với quy tắc, refactor hệ thống để code trở nên đơn giản nhất có thể, bao gồm tất cả các tính năng hiện có. Thực hiện theo quy tắc của `Once And Only Once` và các quy tắc khác để làm cho hệ thống rõ ràng nhất có thể.

### 3.6. Clean code

Code là ngôn ngữ của lập trình viên. Nó là chỉ ngôn ngữ nên không thể nói và chỉ có thể được giao tiếp bằng tay. Là một ngôn ngữ không thể nói, tốt nhất là viết nó tốt và theo cách dễ hiểu. Về mặt kỹ thuật, code phải dễ hiểu đối với người sẽ bảo trì code nói trên.

**1. Comment Your Work**

Comment là rất cần thiết để clean code. Chúng cho phép lập trình viên trình bày ghi chú cho mỗi khối hoặc mỗi dòng code. Làm cho nó dễ hiểu hơn nhiều cho các lập trình viên khi trở lại code sau một thời gian. Ngoài ra, ở nhiều công ty, các lập trình viên của một phần mềm thường không phải là những người bảo trì code, do đó các comments cho phép một nhóm các lập trình viên mới hiểu cách viết code.

![](https://i2.wp.com/simplesnippets.tech/wp-content/uploads/2018/05/comments.jpg?w=804&ssl=1)

**2. Name Them Right!**

Biến là một phần không thể thiếu của các chương trình. Chúng cho phép các giá trị được lưu trữ và thao tác. Trong các chương trình lớn, số lượng input và output lớn do đó cần nhiều biến để thao tác các giá trị này. Các tên được đặt cho các biến này phải phù hợp với các giá trị mà chúng lưu giữ, cho ta phép hiểu rõ hơn về code.

Một điểm quan trọng khác cần ghi nhớ là độ dài của các biến. Đặt tên quá dài sẽ không tốt. Đây là một phần quan trọng trong việc clean code.

**3. Memory Optimization**

Phần mềm ngày nay cần phải được tối ưu hóa cao nếu chúng được phát hành ra bên ngoài. Việc sử dụng tối thiểu các biến và cũng như sử dụng các processes sẽ giúp ích rất nhiều trong việc làm cho các chương trình sử dụng ít không gian bộ nhớ hơn và chạy hiệu quả hơn. Các chương trình ngày nay cần chạy trên các thiết bị nhỏ, nếu code không quản lý tốt bộ nhớ có thể gây ra lag trong các processes có thể ảnh hưởng đến kết quả.

Học cách quản lý bộ nhớ và run-time là một kỹ năng quan trọng.

**4. Make It Functional**

Đưa code được viết vào khối function là cách để code dễ đọc, giúp theo dõi tất cả các quy trình riêng biệt sẽ cho phép viết code được tối ưu hóa hơn.

![](https://i1.wp.com/simplesnippets.tech/wp-content/uploads/2018/05/functions-in-c.jpg?resize=768%2C432&ssl=1)

**5. No Dead Code**

Nhiều lập trình viên trong giai đoạn lập trình comment một vài dòng code, chủ yếu là vì họ chỉ muốn thử nghiệm một tính năng tại một thời điểm. Nếu các comment code vẫn còn trong phiên bản cuối cùng, nó gây ra nhiều khó khăn.

## 4. Bài tập

### 4.1. Predictive text

**Ý tưởng:**

- File chuẩn hóa lại có dạng text [A-z0-9]

- Dùng threadpool để đọc tất cả các file trong folder dataset (blogs). Sau đó cắt ra được các từ trong file, add vào thể hiện duy nhất của Trie và Bloom Filter (Mẫu Singleton).

- Vì kiểm tra từ tồn tại bằng 2 cách (kiểm tra Trie và Bloom Filter), ta áp dụng mẫu Strategy cho 2 cách kiểm tra

- Trie và Bloom Filter implement Dictionary (như gợi ý Tips)

- Với việc gợi ý, dùng Trie, thì với từ đầu vào, ta duyệt từng ký tự tương ứng với các node trong Trie, khi tất cả các ký tự của từ đầu vào đã được mark với các node của Trie, tiếp tục dùng DFS để duyệt xuống các node tiếp theo của Trie cho tới khi isEndOfWord = true. Dĩ nhiên, để biết được node đó đã được duyệt qua, ta đề xuất thêm thuộc tính isVisit.

- Giao diện đơn giản, nhập từ thì sẽ gợi ý các từ ở dưới. Dùng radio button để chọn cách kiểm tra từ.

**Benchmark**

*Config*

```
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
```

*Result*

```
# Warmup: 2 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: vuongnguyen.BenchmarkRunner.benchMarkBloomFilter

# Run progress: 0.00% complete, ETA 00:00:14
# Fork: 1 of 1
# Warmup Iteration   1: 0.068 us/op
# Warmup Iteration   2: 0.069 us/op
Iteration   1: 0.065 us/op
Iteration   2: 0.066 us/op
Iteration   3: 0.066 us/op
Iteration   4: 0.069 us/op
Iteration   5: 0.069 us/op


Result "vuongnguyen.BenchmarkRunner.benchMarkBloomFilter":
  0.067 ±(99.9%) 0.007 us/op [Average]
  (min, avg, max) = (0.065, 0.067, 0.069), stdev = 0.002
  CI (99.9%): [0.060, 0.074] (assumes normal distribution)


# Warmup: 2 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: vuongnguyen.BenchmarkRunner.benchMarkTrie

# Run progress: 50.00% complete, ETA 00:00:08
# Fork: 1 of 1
# Warmup Iteration   1: 0.062 us/op
# Warmup Iteration   2: 0.062 us/op
Iteration   1: 0.079 us/op
Iteration   2: 0.079 us/op
Iteration   3: 0.081 us/op
Iteration   4: 0.072 us/op
Iteration   5: 0.062 us/op


Result "vuongnguyen.BenchmarkRunner.benchMarkTrie":
  0.074 ±(99.9%) 0.030 us/op [Average]
  (min, avg, max) = (0.062, 0.074, 0.081), stdev = 0.008
  CI (99.9%): [0.045, 0.104] (assumes normal distribution)


# Run complete. Total time: 00:00:17

Benchmark                             Mode  Cnt  Score   Error  Units
BenchmarkRunner.benchMarkBloomFilter  avgt    5  0.067 ± 0.007  us/op
BenchmarkRunner.benchMarkTrie         avgt    5  0.074 ± 0.030  us/op
```

### 4.2. Hash Tables

**Delete**

`HashNode DELETED_ITEM = new HashNode(null, null);`

- Dựa vào hàm băm để lấy index của key, từ đó lấy được HashNode item trong mảng bucket

- Một vòng lặp với điều kiện item != null:

	- Nếu item != DELETED_ITEM và key của item trùng với key đang xét thì set tại vị trí index đó là DELETED_ITEM, count--

	- Tiếp tục dùng hàm băm để lấy index và item

**Insert**

- Dựa vào hàm băm để lấy index của key, từ đó lấy được HashNode item trong mảng bucket

- Một vòng lặp với điều kiện item != null:

	- Nếu item != DELETED_ITEM và key của item trùng với key đang xét thì set tại vị trí index đó là item đang xét. Đây là cập nhật item. Return ra khỏi hàm.

	- Tiếp tục dùng hàm băm để lấy index và item

- Lúc này, đã tìm được vị trí còn trống để chèn item vào.

**Search**

- Dựa vào hàm băm để lấy index của key, từ đó lấy được HashNode item trong mảng bucket

- Một vòng lặp với điều kiện item != null:

	- Nếu item != DELETED_ITEM và key của item trùng với key đang xét thì trả về value của item đó.

	- Tiếp tục dùng hàm băm để lấy index và item

- Cuối cùng, nếu không tìm được, trả về null.

**Resize**

- Load bằng số lượng item (khác null và khác DELETED_ITEM)*100/kích thước bucket

	- resize up (base_size * 2) nếu load > 0.7

	- resize (base_size / 2) down nếu load < 0.1

- Resize bằng cách tạo ra bucket array mới, số lượng bucket là một số nguyên tố gần nhất sau base_size

- Bucket array mới sẽ add tất cả các item (khác null và khác DELETED_ITEM) của buecket array cũ vào.

**Collisions**

*Linear probing*

>Nếu colission xuất hiện, index sẽ tăng 1 đơn vị và item sẽ đặt tại bucket có sẵn kế tiếp trong array.

*Quadratic probing*

>Giống với Linear probing, nhưng thay vì đặt item tại bucket có sẵn kế tiếp trong array, index tăng theo kiểu: i, i + 1, i + 4, i + 9, i + 16, ...

*Double hashing*

>Dùng 2 hash function để chọn ra index mới cho item.

### 4.3. Tính thời gian xử lý khiếu nại

**Ý tưởng:**

- Gọi ngày begin và end là ngày "rìa". 

- Ta tính khoảng thời gian làm việc giữa ngày kế tiếp của begin và ngày trước đó của end. Cụ thể, ta sẽ biết được số tuần làm việc (là số nguyên). Sau đó, những ngày dư còn lại trong khoảng, dùng vòng lặp để duyệt, mỗi ngày tương ứng với thứ sẽ có số giờ làm việc khác nhau như đã quy định.

- Tính ngày rìa:

	- Ta có 4 mốc thời gian cụ thể trong ngày làm việc, cần để ý ngày thứ bảy

	- Ngày begin (tính tới): nhìn xem sau ngày begin, đã trải qua bao nhiêu mốc thời gian, từ đó, tính được thời gian làm việc của ngày begin

	- Ngày end (tính lùi): nhìn xem trước end thì đã trải qua bao nhiêu mốc thời gian, từ đó, tính được thời gian làm việc của ngày end

- Riêng nếu, begin và end trong cùng một ngày, thì cả begin và end để tính lùi, rồi lấy kết quả trừ nhau sẽ được đáp án.

**Benchmark**

*Config*

```
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
```

*Result*

```
# Warmup: 2 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: com.zalopay.BenchmarkRunner.benchMarkMethod

# Fork: 1 of 1
# Warmup Iteration   1: 0.685 us/op
# Warmup Iteration   2: 0.474 us/op
Iteration   1: 0.431 us/op
Iteration   2: 0.435 us/op
Iteration   3: 0.437 us/op
Iteration   4: 0.436 us/op
Iteration   5: 0.425 us/op


Result "com.zalopay.BenchmarkRunner.benchMarkMethod":
  0.433 ±(99.9%) 0.019 us/op [Average]
  (min, avg, max) = (0.425, 0.433, 0.437), stdev = 0.005
  CI (99.9%): [0.414, 0.452] (assumes normal distribution)


# Run complete. Total time: 00:00:07

Benchmark                        Mode  Cnt  Score   Error  Units
BenchmarkRunner.benchMarkMethod  avgt    5  0.433 ± 0.019  us/op
```

## 5. Tài liệu tham khảo

https://dzone.com/articles/introduction-probabilistic-0

https://www.slideshare.net/Thnhng44/probabilistic-data-structure-89894194

https://bdupras.github.io/filter-tutorial/

https://www.journaldev.com/1827/java-design-patterns-example-tutorial#singleton-pattern

https://www.makeuseof.com/tag/basic-programming-principles/

https://toidicodedao.com/2015/03/24/solid-la-gi-ap-dung-cac-nguyen-ly-solid-de-tro-thanh-lap-trinh-vien-code-cung/

https://dzone.com/articles/software-design-principles-dry-and-kiss

https://simplesnippets.tech/how-to-write-clean-code-top-5-ways/