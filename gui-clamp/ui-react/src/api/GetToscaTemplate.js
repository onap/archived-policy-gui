import React, { useState } from "react";
import Button from "react-bootstrap/Button";

const GetToscaTemplate = (props) => {

  const [windowLocationPathName, setWindowLocationPathname] = useState('');

  const getTemplateHandler = async () => {

    setWindowLocationPathname(window.location.pathname);

    const params = {
      name: props.templateName,
      version: props.templateVersion
    }

    const response = await fetch(windowLocationPathName +
      '/restservices/clds/v2/toscaControlLoop/getToscaTemplate' + '?' + (new URLSearchParams(params)));

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
