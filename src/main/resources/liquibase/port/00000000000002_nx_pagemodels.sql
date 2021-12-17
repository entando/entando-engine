INSERT INTO pagemodels (code,descr,frames,plugincode,templategui, type, locked) VALUES ('nx-page-template','NxPage Template','<?xml version="1.0" encoding="UTF-8"?>
<frames>
</frames>

',NULL,'<#assign wp=JspTaglibs["/aps-core"]>
<@wp.info key="systemParam" paramName="applicationBaseURL" var="appUrl" />

<html lang="en">
    <head>
        <meta charset="utf-8" />
        <title>
            <@wp.currentPage param="title" />
        </title>
        <meta name="viewport" content="width=device-width,  user-scalable=no" />
        <link rel="icon" href="${appUrl}favicon.png" type="image/png" />

        </head>
        <body>
        </body>
</html>
', 'NX', 1);
