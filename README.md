# OneMDMInstaller

A library which checks whether OneMDM is installed.

# Integrating the Library with Existing Project

To integrate OneMDM Installer with an existing project:

1. Clone this repository: `git clone https://github.com/multunus/onemdm-installer.git`
2. Open the existing project in Android Studio
3. Goto `File > New > Import Modules` and choose onemdm-installer. Make sure the name of the module imported is `onemdm-installer`
4. In the application’s main activity `onCreate` method, start the onemdm-installer service as follows:

``` java
public void onCreate() {
	...
	Intent intent = new Intent(this, OneMDMInstallerService.class);
	startService(intent);
}
```
