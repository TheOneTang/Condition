import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NewLock {
	public static void main(String[] args)
	{
		PrintMachine p=new PrintMachine();
		new No1(p).start();
		new No2(p).start();
	}
}
class No1 extends Thread{
	private PrintMachine p;
	public No1(PrintMachine p)
	{
		this.p=p;
	}
	public void run(){
		for(int i=0;i<52;i++)
		p.showNum();
	}
}
class No2 extends Thread{
	private PrintMachine p;
	public No2(PrintMachine p)
	{
		this.p=p;
	}
	public void run(){
		for(int i=0;i<52;i++)
		p.showChar();
	}
}
class PrintMachine{
	private Lock lock=new ReentrantLock();
	private Condition PrintChar=lock.newCondition();
	private Condition PrintNum=lock.newCondition();
	private boolean flag=false;
	private char Print_C='A';
	private int Print_N=1;
	public PrintMachine(){}
	public void showNum(){
		try{
			//获取对该对象的锁定
			lock.lock();
			//判断输出的条件，若flag为false则输出
			if(!flag)
			{
				for(int i=0;i<2;i++)
				{
					System.out.print(Print_N++);
				}
				flag=true;
				PrintChar.signal();
			}
			else
			{
				try{
					PrintNum.await();
				}catch(InterruptedException e)
				{
					System.out.println(e.getMessage());
				}
			}
		}finally{
			lock.unlock();
		}
	}
	//打印字符
	public void showChar(){
		try{
			lock.lock();
			if(flag)
			{
				System.out.print(Print_C++);
				flag=false;
				PrintNum.signal();
			}
			else
			{
				try{
					PrintChar.await();
				}catch(InterruptedException e)
				{
					System.out.println(e.getMessage());
				}
			}
		}finally
		{
			lock.unlock();
		}
	}
}
