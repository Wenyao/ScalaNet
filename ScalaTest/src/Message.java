import java.io.*;

/**
 * This is a representation of a node of the distributed system.
 */
public class Message implements Serializable {
	private static final long serialVersionUID = -1L;
	private static int ID = 0; // absolute unique id
	private String src;
	private String dest;
	private String kind;
	private Object data;
	private int id;

	/**
	 * Constructor
	 * @param src
	 * @param dest
	 * @param kind
	 * @param data
	 */
	public Message(String src, String dest, String kind, Object data) {
		this.src = src;
		this.dest = dest;
		this.kind = kind;
		this.data = data;
	}
	
	/* Accessors */
	public int    getId()       { return id;   }	
	public String getSrc()      { return src;  }
	public String getDst()      { return dest; }
	public String getKind()     { return kind; }
	public Object getData()     { return data; }
	public void setId() 			      { this.id  = ID++;      }
	public void setSrc(String src)        { this.src = src;       }
	public void setDest(String dest)      { this.dest = dest;     }
	public void setKind(String kind)      { this.kind = kind;     }
	public void setData(Object data)      { this.data = data;     }


	@Override
	public String toString() {
		return "ID: " + this.id + "; From " + this.src + " to " + this.dest + "; " 
		+ this.kind + "; " + this.data;
	}
	
	/**
	 * get the length of packet payload
	 * @param obj
	 * @return
	 */
	protected static byte[] toByteArray (Object obj) {
		byte[] bytes = null;
		ByteArrayOutputStream baos;
		ObjectOutputStream oos;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos); 
			oos.writeObject(obj);
			oos.flush(); 
			oos.close(); 
			baos.close();
			bytes = baos.toByteArray ();
		}
		catch (IOException e) {
			System.out.println("error in toByteArray()");
		}
		return bytes;
	}
}
