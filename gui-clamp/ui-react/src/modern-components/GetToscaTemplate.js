import React from "react";
import Button from "react-bootstrap/Button";

const GetToscaTemplate = (props) => {

  const getTemplateHandler = async () => {

    const params = {
      name: props.templateName,
      version: props.templateVersion
    }

    const response = await fetch('https://localhost:3000' +
      '/onap/controlloop/v2/commission/' + 'toscaservicetemplate'
      + '?' + (new URLSearchParams(params)), {
      headers: {
        'Authorization': 'Basic ' + btoa('healthcheck' + ':' + 'zb!XztG34')
      }
    });

    const data = await response.json();

    props.onGetToscaServiceTemplate(data);

  }

  return (
    <React.Fragment>
      <Button variant="primary"
              type="submit"
              onClick={ getTemplateHandler }>Get Tosca Service Template</Button>
    </React.Fragment>
  );


}

export default GetToscaTemplate;