# OneMDMInstaller

A library which checks whether [OneMDM](http://multunus.github.io/onemdm-server/) is installed.

### Integrating the Library with Existing Project

To integrate OneMDM Installer with an existing project:

1. Add the library in the dependencies section:

    ``` gradle
    dependencies {
	    ...
	    compile 'com.multunus:onemdm-installer:0.1'
    }
    ```
1. In the application’s main activity `onCreate` method, start the onemdm-installer service as follows:

	``` java
	public void onCreate() {
		...
		Intent intent = new Intent(this, OneMDMInstallerService.class);
		startService(intent);
	}
	```
