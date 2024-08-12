syndicate export --resource_type api_gateway --dsl oas_v3
syndicate generate meta s3_bucket --resource_name api-ui-hoster --acl public-read --static_website_hosting true

syndicate generate swagger_ui --name task11_api_ui --path_to_spec export/zrhev5wrua_oas_v3.json --target_bucket api-ui-hoster