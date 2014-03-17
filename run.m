clc;
close all;
clear all;
if ~compile(), return; end;

jc = View3();
[~, hCanvas] = javacomponent(jc);
set(hCanvas, 'Units', 'normalized', 'Position', [0 0 1 1]);

% load data
load sphere.mat M;

% data expexted as: [x1 y1 z1  x2 y2 z2 ...] 
verts = single(M.vertices)';
normals = single(M.normals)';
faces = (int32(M.faces)-1)';
% with normals
% jc.add_mesh(verts(:),faces(:),normals(:));
% Without normals
% jc.add_mesh(verts(:),faces(:));

% jc.add(CubeRenderer());

npoints = 100000;
vcolors = single(randn(npoints,3))';
vpoints = single(randn(npoints,3))';
cloud = jc.scatter(vpoints(:), vcolors(:));

cloud.setPointSize(5);