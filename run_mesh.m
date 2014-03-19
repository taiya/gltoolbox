clc; close all; clear all;
if ~compile(), return; end;

load sphere.mat M;
view = View3;
view.mesh(M.vertices, M.faces, M.normals); % shaded
% view.mesh(M.vertices, M.faces); % TODO: unshaded
% TODO: with face/vertex colors
