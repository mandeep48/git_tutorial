
public class MaxMin {

	
	
	public static void main(String[] args) {
		
		
		int a[]= {1, 3,20, 4, 1, 0};
		int n=a.length;
		System.out.println("peak----------------"+findPeakUtil(a,0,n-1,n));
		
		/*int max= -1;
		int min = Integer.MAX_VALUE;
		
		for (int i =0;i<a.length;i++){
		//	System.out.println(i);
			//System.out.println(a[i]);
			if (max < a[i]) {
				max = a[i];
			}  else if (min > a[i]) {
				min = a[i];
			}
			
			
		}
		System.out.println("max : "+max + " min : "+min);*/
		
	}
	
	static int findPeakUtil(int arr[], int low, int high, int n)
    {
        // Find index of middle element
        int mid = low + (high - low)/2;  /* (low + high)/2 */
 
        // Compare middle element with its neighbours (if neighbours
        // exist)
        if ((mid == 0 || arr[mid-1] <= arr[mid]) && (mid == n-1 ||
             arr[mid+1] <= arr[mid]))
            return mid;
 
        // If middle element is not peak and its left neighbor is
        // greater than it,then left half must have a peak element
        else if (mid > 0 && arr[mid-1] > arr[mid])
            return findPeakUtil(arr, low, (mid -1), n);
 
        // If middle element is not peak and its right neighbor
        // is greater than it, then right half must have a peak
        // element
        else return findPeakUtil(arr, (mid + 1), high, n);
    }
}
