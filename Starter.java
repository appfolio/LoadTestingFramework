
import java.io.File;
import java.util.Scanner;

public class Starter {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		String[] fileNames = args;
		boolean success = true;
		System.out.println("##teamcity[testSuiteStarted name='Load Tests']");
		
		for(String fileName : fileNames){
			fileName = fileName.substring(0,fileName.lastIndexOf("."));
			Process p = Runtime.getRuntime().exec("./apache-jmeter-2.7/bin/jmeter -n -t TestPlans/"+fileName+".jmx -l "+fileName+".csv");//make the actual jmeter test command here
			System.out.println("##teamcity[testStarted name='test"+fileName+"']");
			int response_code = p.waitFor();
			//get xml/csv file produced by test in useful format
			Scanner file = new Scanner(new File(fileName+".csv"));
			//process file for success/failure, build statistics
			int totalTime = -1;
			int count = 0;
			while(file.hasNextLine()){
				String[] result = file.nextLine().split(",");
				count++;
				if(result[7].equals("false")) success = false;
				totalTime += Integer.valueOf(result[1]);
				
			}
			//write out success and average time
			if(!success)
			{
				System.out.println("##teamcity[testFailed name='test"+fileName+
						"' message='Some load test had failing calls' details='At least one sample was marked unsuccessful']");
			}
			System.out.println("##teamcity[testFinished name='test"+fileName+"']");
			System.out.println("##teamcity[buildStatisticValue key='"+fileName+" Average Time' value='"+totalTime/count+"']");
		}
		
		System.out.println("##teamcity[testSuiteFinished name='Load Tests']");
	}

}
