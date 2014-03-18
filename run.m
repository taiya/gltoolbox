clc; close all; clear all;
if ~compile(), return; end;

% Create an OpenGL window

%--- Shows a (colored) point cloud
% npoints = 10000;
% vpoints = .2*( randn(npoints,3) ) ;
% vcolors = rand(npoints,3);

% view = View3;
% cloud = view.scatter(vpoints,vcolors);
% cloud.setPointSize(5);

%--- Shows a mesh
% load sphere.mat M;
% view.mesh(M.vertices, M.faces); % TODO: unshaded
% view.mesh(M.vertices, M.faces, M.normals); % shaded


scatter3(vpoints(:,1),vpoints(:,2),vpoints(:,3),1,vcolors)