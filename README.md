# hcl-dx-google-vertex-sample

![image](https://github.com/HCL-TECH-SOFTWARE/dx-google-vertex-sample/assets/7836735/019d0f19-5352-4e14-9f26-039671c05f6c)

Combined with:

![image](https://github.com/HCL-TECH-SOFTWARE/dx-google-vertex-sample/assets/7836735/a15f6c8d-c21f-4fca-8049-2cad9fcee8cd)

Built with:

![image](https://github.com/HCL-TECH-SOFTWARE/dx-google-vertex-sample/assets/7836735/904afc19-bdcb-45ea-a3c1-4b2a554adff4)


This project contains the source code and build for a sample AI integration for DX for WCM following the model established in https://opensource.hcltechsw.com/digital-experience/latest/get_started/plan_deployment/container_deployment/wcm_content_ai_analysis.html?h=analysis#configuring-ai-class-for-custom-content-ai-provider.

It leverages Google Vertex AI for sentiment analysis, summary and keyword generation.

The build of the project is established via maven.

## Code Adjustment

Adjust the following for your project and region:
```
	private static final String PROJECT = "your-project-id-google";
	private static final String LOCATION = "us-central1";
```

## Deployment

Use mvn package and copy the resulting jar file to the DX shared library - e.g. on a container to /opt/HCL/wp_profile/PortalServer/sharedLibrary.
Also include any required jar files - e.g. for the google maven dependency. 
Not sure how to get those? A command like mvn dependency:copy-dependencies will download all the dependent jar files to the target/dependency directory.


Generate a service account key and export the key to DX.
![image](https://github.com/HCL-TECH-SOFTWARE/dx-google-vertex-sample/assets/7836735/57340eb5-cf4c-47a3-96c3-d6c8b95198f0)


Configure a WAS environment variable with defined service account 
![image](https://github.com/HCL-TECH-SOFTWARE/dx-google-vertex-sample/assets/7836735/9e835e98-38c1-4697-9ce2-521f85463b57)


Follow the steps for https://opensource.hcltechsw.com/digital-experience/latest/get_started/plan_deployment/container_deployment/wcm_content_ai_analysis.html?h=analysis#configuring-ai-class-for-custom-content-ai-provider passing the classname as com.hcl.GoogleVertexAnalyzerSample.
I.e. it would look like this:
![image](https://github.com/HCL-TECH-SOFTWARE/dx-google-vertex-sample/assets/7836735/fab3c8db-58df-4d09-b873-1c2b1a7648bb)


Restart.

## Contributions and Feedback

Your input holds immense value to us. We welcome contributions, suggestions, and inquiries aimed at refining our documentation, configuration or implementation. Should you seek to extend this resource or require clarification, please let us know through issues, pull requests or directly reaching out to our core contributors (refer to the page [CONTRIBUTING](./CONTRIBUTING.md) for more details). HCL will make every reasonable effort to assist in problem resolution of any issues found in this software.

## Support

In case of questions or issues please raise via Issues tab in this github repository. HCL Support will make every reasonable effort to assist in problem resolution of any issues found in this software.
