{
  description = "AGISIT project management flake";
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { nixpkgs, flake-utils, ... }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; config = {
              allowUnfree = true;
            };
          };
      in
      {
        devShell = pkgs.mkShell {
          buildInputs = with pkgs; [
            kubectl
            terraform
            (google-cloud-sdk.withExtraComponents (with google-cloud-sdk.components; [
              gke-gcloud-auth-plugin
            ]))
          ];
          
        };
      }
    );
}