clc; close all; clear all;
if ~compile(), return; end;

% Create an OpenGL window
view = View3;

%--- Shows a (colored) point cloud
npoints = 100000;
vpoints = .2*( randn(npoints,3) ) ;
vcolors = rand(npoints,3);
cloud = view.scatter(vpoints,vcolors);
cloud.setPointSize(5);

%--- Shows a mesh
% load sphere.mat M;
% view.mesh(M.vertices, M.faces); % TODO: unshaded
% view.mesh(M.vertices, M.faces, M.normals); % shaded

