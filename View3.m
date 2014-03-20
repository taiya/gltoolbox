classdef View3
    properties
        jCanvas = [];
        container = [];
        hfigure = [];
        
        %--- Menu
        hmenu = [];
        hmenu_view = [];
    end
    
    methods
        %--- Constructor
        function this = View3()
            %--- Instantiate Java/Matlab resources
            this.hfigure = figure();
            this.jCanvas = OpenGLCanvas();
            %--- place the given Java component in the current figure
            [~, this.container] = javacomponent(this.jCanvas,[],this.hfigure);
            %--- make the java object occupy the full figure
            set(this.container,'Units', 'normalized');
            set(this.container,'Position', [0 0 1 1]); 
            %--- delete the old menu
            set(this.hfigure,'Toolbar','none')
            set(this.hfigure,'Menubar','none');
            %--- setup the menu
            this.hmenu_view = uimenu(this.hfigure,'Label','View');
            uimenu(this.hmenu_view,'Label','Reset Arcball','Callback',@this.hmenu_view_cb);
        end
        
        function ret = mesh(this, vertices, faces, normals)
            % data expexted as: [x1 y1 z1  x2 y2 z2 ...] 
            verts = single(vertices)'; 
            faces = (int32(faces)-1)'; %remove 1-indexing
            normals = single(normals)';
            ret = Mesh(verts(:),faces(:),normals(:));
            this.jCanvas.add(ret);
        end
        
        function ret = scatter(this, vpoints, vcolors)
            vpoints = single(vpoints)';
            vcolors = single(vcolors)';
            ret = PointCloud(vpoints(:), vcolors(:));
            this.jCanvas.add(ret);
        end
        
        function hmenu_view_cb(this, src, eventdata) %#ok<INUSD>
            disp('callback!');
        end
        
        %--- adds a simple cube to the drawer
        function ret = cube(this)
            ret = this.jCanvas.add(CubeRenderer());
        end
    end
        
    %--- Static methods        
    methods(Static)
        compile();
    end
end